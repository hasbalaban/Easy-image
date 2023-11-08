package com.easyImage.mediapi.repository.video

import javax.inject.Inject

class LocalVideoDataSource(
    private val videoService: VideoService
): VideoDataOperation {
    override suspend fun getVideos(
        key: String,
        query: String?,
        page: Int
    ): Response<WrapResponse<VideoResponse>> {
        return videoService.getVideos(
            key = key,
            query = query,
            page = page
        )
    }
}