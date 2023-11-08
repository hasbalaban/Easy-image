package com.easyImage.mediapi.repository.video

import com.easyImage.mediapi.model.VideoResponse
import com.easyImage.mediapi.utils.WrapResponse
import retrofit2.Response

interface VideoDataOperation {

    suspend fun getVideos(
        key: String,
        query: String?,
        page: Int
    ): Response<WrapResponse<VideoResponse>>?

}