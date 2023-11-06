package com.easyImage.mediapi.model

data class VideoResponse(
    val total: Int?,
    val totalHits: Int?,
    val hits: List<VideoItem>?
)

data class VideoItem(
    val id: Int,
    val pageURL: String?,
    val type: String?,
    val tags: String?,
    val duration: Int?,
    val picture_id: String?,
    val videos: VideoUrls?,
    val views: Int?,
    val downloads: Int?,
    val likes: Int?,
    val comments: Int?,
    val user_id: Int?,
    val user: String?,
    val userImageURL: String?
)

data class VideoUrls(
    val large: VideoSize?,
    val medium: VideoSize?,
    val small: VideoSize?,
    val tiny: VideoSize?
)

data class VideoSize(
    val url: String?,
    val width: Int?,
    val height: Int?,
    val size: Int?
)

data class VideoItemDTO(
    val id : Int,
    val videoPreviewUrl : String,
    val videoUrl : String,
    val isMusicOpen : Boolean = false,
    val videoTag : String
)
