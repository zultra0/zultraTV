package com.zultra.zultratv.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.zultra.zultratv.data.api.XtreamCodesApi
import com.zultra.zultratv.data.model.MediaCategory
import com.zultra.zultratv.data.model.VodItem
import com.zultra.zultratv.data.parser.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.util.concurrent.TimeUnit

enum class PortalType {
    STALKER_MAC,
    XTREAM_CODES,
    M3U_URL
}

data class PortalConfig(
    val type: PortalType,
    val serverUrl: String,
    val macAddress: String = "",
    val username: String = "",
    val password: String = ""
)

class IptvRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("iptv_settings", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun savePortal(config: PortalConfig) {
        prefs.edit().putString("saved_portal", gson.toJson(config)).apply()
    }

    fun getSavedPortal(): PortalConfig? {
        val json = prefs.getString("saved_portal", null) ?: return null
        return try {
            gson.fromJson(json, PortalConfig::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearPortal() {
        prefs.edit().remove("saved_portal").apply()
    }

    suspend fun loadContent(): List<MediaCategory> = withContext(Dispatchers.IO) {
        val portal = getSavedPortal() ?: return@withContext emptyList()
        when (portal.type) {
            PortalType.STALKER_MAC -> loadStalkerContent(portal)
            PortalType.XTREAM_CODES -> loadXtreamContent(portal)
            PortalType.M3U_URL -> loadM3uContent(portal.serverUrl)
        }
    }

    private suspend fun loadStalkerContent(portal: PortalConfig): List<MediaCategory> {
        val cleanUrl = portal.serverUrl.trimEnd('/')
        val portalUrl = if (cleanUrl.contains("/server/load.php")) {
            cleanUrl
        } else if (cleanUrl.contains("/c/")) {
            cleanUrl.replace("/c/", "/server/load.php")
        } else {
            "$cleanUrl/server/load.php"
        }

        val mac = portal.macAddress.trim()
        val cookie = "mac=$mac; stb_lang=en; timezone=GMT"
        val userAgent = "Mozilla/5.0 (QtEmbedded; U; Linux; C) AppleWebKit/533.3 (KHTML, like Gecko) MAG250 stbapp ver: 2 rev: 250 Safari/533.3"

        // 1. Handshake to get token
        var token = ""
        try {
            val handshakeUrl = "$portalUrl?type=stb&action=handshake&token="
            val req = Request.Builder()
                .url(handshakeUrl)
                .header("User-Agent", userAgent)
                .header("Cookie", cookie)
                .build()

            val res = httpClient.newCall(req).execute()
            val body = res.body?.string() ?: ""
            val json = JSONObject(body)
            if (json.has("js") && json.getJSONObject("js").has("token")) {
                token = json.getJSONObject("js").getString("token")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Fetch Live Channel Categories and Streams
        val categories = mutableListOf<MediaCategory>()
        try {
            val channelUrl = "$portalUrl?type=itv&action=get_all_channels"
            val reqBuilder = Request.Builder()
                .url(channelUrl)
                .header("User-Agent", userAgent)
                .header("Cookie", cookie)

            if (token.isNotEmpty()) {
                reqBuilder.header("Authorization", "Bearer $token")
            }

            val res = httpClient.newCall(reqBuilder.build()).execute()
            val body = res.body?.string() ?: ""
            val json = JSONObject(body)
            val dataArray = if (json.has("js") && json.getJSONObject("js").has("data")) {
                json.getJSONObject("js").getJSONArray("data")
            } else if (json.has("data")) {
                json.getJSONArray("data")
            } else {
                JSONArray()
            }

            val channelList = mutableListOf<VodItem>()
            for (i in 0 until minOf(dataArray.length(), 60)) {
                val item = dataArray.getJSONObject(i)
                val id = item.optString("id", i.toString())
                val name = item.optString("name", "Channel $i")
                val logo = item.optString("logo", "").takeIf { it.isNotEmpty() }
                val cmd = item.optString("cmd", "")

                val streamUrl = if (cmd.startsWith("ffmpeg ")) {
                    cmd.substringAfter("ffmpeg ").trim()
                } else if (cmd.startsWith("auto ")) {
                    cmd.substringAfter("auto ").trim()
                } else {
                    cmd
                }

                if (streamUrl.isNotEmpty()) {
                    channelList.add(
                        VodItem(
                            id = "stalker_$id",
                            title = name,
                            posterUrl = logo,
                            backdropUrl = logo,
                            streamUrl = streamUrl,
                            rating = "Live TV",
                            year = "Live"
                        )
                    )
                }
            }

            if (channelList.isNotEmpty()) {
                categories.add(MediaCategory(title = "Live Stalker Channels", items = channelList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return categories
    }

    private suspend fun loadXtreamContent(portal: PortalConfig): List<MediaCategory> {
        val baseUrl = if (portal.serverUrl.endsWith("/")) portal.serverUrl else "${portal.serverUrl}/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(XtreamCodesApi::class.java)

        val categories = mutableListOf<MediaCategory>()

        try {
            val liveStreams = api.getLiveStreams(portal.username, portal.password)
            val liveItems = liveStreams.take(50).map { stream ->
                val streamUrl = "${baseUrl}live/${portal.username}/${portal.password}/${stream.streamId}.ts"
                VodItem(
                    id = "live_${stream.streamId}",
                    title = stream.name,
                    posterUrl = stream.streamIcon,
                    backdropUrl = stream.streamIcon,
                    streamUrl = streamUrl,
                    rating = "Live TV",
                    year = "Live"
                )
            }
            if (liveItems.isNotEmpty()) {
                categories.add(MediaCategory(title = "Live Channels", items = liveItems))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val vodStreams = api.getVodStreams(portal.username, portal.password)
            val vodItems = vodStreams.take(50).map { stream ->
                val streamUrl = "${baseUrl}movie/${portal.username}/${portal.password}/${stream.streamId}.mp4"
                VodItem(
                    id = "vod_${stream.streamId}",
                    title = stream.name,
                    posterUrl = stream.streamIcon,
                    backdropUrl = stream.streamIcon,
                    streamUrl = streamUrl,
                    rating = stream.rating?.let { "$it ⭐" } ?: "VOD",
                    year = "Movie"
                )
            }
            if (vodItems.isNotEmpty()) {
                categories.add(MediaCategory(title = "Movies & VOD", items = vodItems))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return categories
    }

    private suspend fun loadM3uContent(m3uUrl: String): List<MediaCategory> {
        val request = Request.Builder().url(m3uUrl).build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return emptyList()

        val inputStream: InputStream = response.body?.byteStream() ?: return emptyList()
        val channels = M3uParser.parse(inputStream)

        return channels.groupBy { it.category }
            .map { (catName, channelList) ->
                MediaCategory(
                    title = catName,
                    items = channelList.map { ch ->
                        VodItem(
                            id = ch.id,
                            title = ch.name,
                            posterUrl = ch.logoUrl,
                            backdropUrl = ch.logoUrl,
                            streamUrl = ch.streamUrl,
                            rating = "Live TV",
                            year = "Live"
                        )
                    }
                )
            }
    }
}
