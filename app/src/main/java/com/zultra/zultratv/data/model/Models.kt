package com.zultra.zultratv.data.model

enum class MediaType {
    LIVE_TV,
    MOVIE,
    SERIES,
    LOCAL_VIDEO,
    LOCAL_MUSIC
}

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val category: String = "General",
    val epgId: String? = null
)

data class VodItem(
    val id: String,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val streamUrl: String,
    val rating: String? = null,
    val description: String? = null,
    val genre: String = "Movies",
    val year: String? = null,
    val duration: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val mediaType: MediaType = MediaType.MOVIE
)

data class MediaCategory(
    val title: String,
    val items: List<VodItem>
)
