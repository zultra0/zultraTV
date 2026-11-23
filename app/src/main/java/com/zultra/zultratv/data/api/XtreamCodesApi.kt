package com.zultra.zultratv.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface XtreamCodesApi {
    @GET("player_api.php")
    suspend fun authenticate(
        @Query("username") user: String,
        @Query("password") pass: String
    ): XtreamAuthResponse

    @GET("player_api.php")
    suspend fun getLiveStreams(
        @Query("username") user: String,
        @Query("password") pass: String,
        @Query("action") action: String = "get_live_streams"
    ): List<XtreamLiveStream>

    @GET("player_api.php")
    suspend fun getVodStreams(
        @Query("username") user: String,
        @Query("password") pass: String,
        @Query("action") action: String = "get_vod_streams"
    ): List<XtreamVodStream>
}

data class XtreamAuthResponse(
    @SerializedName("user_info") val userInfo: XtreamUserInfo?,
    @SerializedName("server_info") val serverInfo: XtreamServerInfo?
)

data class XtreamUserInfo(
    @SerializedName("username") val username: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("exp_date") val expDate: String?
)

data class XtreamServerInfo(
    @SerializedName("url") val url: String?,
    @SerializedName("port") val port: String?
)

data class XtreamLiveStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("category_id") val categoryId: String?
)

data class XtreamVodStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("category_id") val categoryId: String?
)
