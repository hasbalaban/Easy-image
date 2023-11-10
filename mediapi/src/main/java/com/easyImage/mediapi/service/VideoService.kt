package com.easyImage.mediapi.service

import com.easyImage.mediapi.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {
    @GET("api/videos")
    suspend fun getVideos(
        @Query("key") key: String,
        @Query("q") query: String?,
        @Query("page") page: Int
    ) : VideoResponse
}