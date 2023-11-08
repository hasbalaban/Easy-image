package com.easyImage.mediapi.repository.video

import com.easyImage.mediapi.hiltmodules.AppConstants
import com.easyImage.mediapi.model.VideoResponse
import com.easyImage.mediapi.service.VideoService
import com.easyImage.mediapi.utils.WrapResponse
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LocalVideoDataSource @Inject constructor(

): VideoDataOperation {
    override suspend fun getVideos(
        key: String,
        query: String?,
        page: Int
    ): Response<WrapResponse<VideoResponse>> {
        val gson = GsonBuilder().create()

        val videoService = Retrofit.Builder()
            .baseUrl(AppConstants.TEST_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(VideoService::class.java)

        return Response.success(
            WrapResponse<VideoResponse>(
                data = videoService.getVideos(key = key, query = query, page = page),
                success = true,
            )
        )
    }
}