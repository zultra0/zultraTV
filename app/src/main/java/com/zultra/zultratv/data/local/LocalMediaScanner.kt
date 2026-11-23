package com.zultra.zultratv.data.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.zultra.zultratv.data.metadata.OnlineMetadataSearcher
import com.zultra.zultratv.data.model.MediaCategory
import com.zultra.zultratv.data.model.MediaType
import com.zultra.zultratv.data.model.VodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalMediaScanner(private val context: Context) {

    // Scan Videos (Movies / TV Shows) from device storage & fetch database metadata
    suspend fun scanLocalVideos(): MediaCategory = withContext(Dispatchers.IO) {
        val items = mutableListOf<VodItem>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION
        )
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            while (it.moveToNext() && items.size < 40) {
                val id = it.getLong(idCol)
                val displayName = it.getString(nameCol) ?: "Video $id"
                val rawTitle = it.getString(titleCol) ?: displayName
                val durationMs = it.getLong(durationCol)
                val durationMin = if (durationMs > 0) "${durationMs / (1000 * 60)}m" else "HD"
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                // Query movie database for posters and summary
                val meta = OnlineMetadataSearcher.searchMovie(rawTitle)
                val cleanTitle = OnlineMetadataSearcher.cleanMediaTitle(rawTitle)

                items.add(
                    VodItem(
                        id = "local_vid_$id",
                        title = meta?.title ?: cleanTitle,
                        posterUrl = meta?.posterUrl,
                        backdropUrl = meta?.backdropUrl,
                        streamUrl = contentUri.toString(),
                        rating = meta?.rating ?: "Local Movie",
                        description = meta?.description ?: "Stored locally on your device in high definition.",
                        genre = meta?.genre ?: "Local Video",
                        year = meta?.year ?: durationMin,
                        duration = durationMin,
                        mediaType = MediaType.LOCAL_VIDEO
                    )
                )
            }
        }

        MediaCategory(
            title = "My Local Movies & Videos",
            items = items
        )
    }

    // Scan Music (Tracks / Albums) from device storage & search cover art from music database
    suspend fun scanLocalMusic(): MediaCategory = withContext(Dispatchers.IO) {
        val items = mutableListOf<VodItem>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = context.contentResolver.query(
            uri,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext() && items.size < 40) {
                val id = it.getLong(idCol)
                val title = it.getString(titleCol) ?: "Track $id"
                val artist = it.getString(artistCol) ?: "Unknown Artist"
                val album = it.getString(albumCol) ?: "Local Music"
                val durationMs = it.getLong(durationCol)
                val durationMin = if (durationMs > 0) "${durationMs / (1000 * 60)}:${String.format("%02d", (durationMs / 1000) % 60)}" else "Audio"
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

                // Search music database for high-res album cover
                val meta = OnlineMetadataSearcher.searchMusic(title, artist)

                items.add(
                    VodItem(
                        id = "local_music_$id",
                        title = meta?.title ?: title,
                        posterUrl = meta?.coverUrl,
                        backdropUrl = meta?.coverUrl,
                        streamUrl = contentUri.toString(),
                        rating = "Music",
                        description = "Artist: ${meta?.artist ?: artist} • Album: ${meta?.album ?: album}",
                        genre = "Music",
                        year = durationMin,
                        artist = meta?.artist ?: artist,
                        album = meta?.album ?: album,
                        duration = durationMin,
                        mediaType = MediaType.LOCAL_MUSIC
                    )
                )
            }
        }

        MediaCategory(
            title = "My Music & Albums",
            items = items
        )
    }
}
