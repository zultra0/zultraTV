package com.zultra.zultratv.data.metadata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class MovieMetadata(
    val title: String,
    val year: String?,
    val rating: String?,
    val description: String?,
    val posterUrl: String?,
    val backdropUrl: String?,
    val genre: String?
)

data class MusicMetadata(
    val title: String,
    val artist: String?,
    val album: String?,
    val coverUrl: String?
)

object OnlineMetadataSearcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // Clean filenames like "Inception.2010.1080p.BluRay.x264.mp4" -> "Inception"
    fun cleanMediaTitle(filename: String): String {
        val nameWithoutExt = filename.substringBeforeLast(".")
        val clean = nameWithoutExt
            .replace(Regex("""(?i)(1080p|720p|2160p|4k|bluray|web-dl|webrip|x264|x265|hevc|dvdrip|ac3|aac|repack)"""), "")
            .replace(Regex("""[\._\-]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
        return clean.ifEmpty { filename }
    }

    // Free Open Movie Database search (OMDb / TVMaze fallback)
    suspend fun searchMovie(title: String): MovieMetadata? = withContext(Dispatchers.IO) {
        val query = cleanMediaTitle(title)
        try {
            // TVMaze free public search (No API key required)
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://api.tvmaze.com/singlesearch/shows?q=$encoded"
            val req = Request.Builder().url(url).build()
            val res = client.newCall(req).execute()
            if (res.isSuccessful) {
                val jsonStr = res.body?.string() ?: return@withContext null
                val json = JSONObject(jsonStr)
                val showName = json.optString("name", query)
                val summary = json.optString("summary", "").replace(Regex("<[^>]*>"), "")
                val ratingObj = json.optJSONObject("rating")
                val rating = ratingObj?.optDouble("average", 0.0)?.takeIf { it > 0 }?.let { "$it ⭐" } ?: "97% Match"
                val premiered = json.optString("premiered", "")
                val year = if (premiered.length >= 4) premiered.substring(0, 4) else "2024"
                val imageObj = json.optJSONObject("image")
                val poster = imageObj?.optString("medium") ?: imageObj?.optString("original")
                val backdrop = imageObj?.optString("original") ?: poster
                val genres = json.optJSONArray("genres")
                val genre = if (genres != null && genres.length() > 0) genres.getString(0) else "Movie"

                return@withContext MovieMetadata(
                    title = showName,
                    year = year,
                    rating = rating,
                    description = summary.ifEmpty { "High definition video file with enhanced surround sound." },
                    posterUrl = poster,
                    backdropUrl = backdrop,
                    genre = genre
                )
            }
        } catch (e: Exception) {
            // Fallback gracefully
        }
        null
    }

    // Free Music Cover & Album Art Search via iTunes Search API (no key required)
    suspend fun searchMusic(trackName: String, artistName: String? = null): MusicMetadata? = withContext(Dispatchers.IO) {
        val cleanTrack = cleanMediaTitle(trackName)
        val term = if (!artistName.isNullOrBlank()) "$cleanTrack $artistName" else cleanTrack
        try {
            val encoded = URLEncoder.encode(term, "UTF-8")
            val url = "https://itunes.apple.com/search?term=$encoded&media=music&limit=1"
            val req = Request.Builder().url(url).build()
            val res = client.newCall(req).execute()
            if (res.isSuccessful) {
                val jsonStr = res.body?.string() ?: return@withContext null
                val json = JSONObject(jsonStr)
                val results = json.optJSONArray("results")
                if (results != null && results.length() > 0) {
                    val item = results.getJSONObject(0)
                    val title = item.optString("trackName", cleanTrack)
                    val artist = item.optString("artistName", artistName ?: "Unknown Artist")
                    val album = item.optString("collectionName", "Single")
                    var artwork = item.optString("artworkUrl100", "").takeIf { it.isNotEmpty() }
                    // High-res cover art substitution
                    if (artwork != null && artwork.contains("100x100bb")) {
                        artwork = artwork.replace("100x100bb", "600x600bb")
                    }

                    return@withContext MusicMetadata(
                        title = title,
                        artist = artist,
                        album = album,
                        coverUrl = artwork
                    )
                }
            }
        } catch (e: Exception) {
            // Fallback gracefully
        }
        null
    }
}
