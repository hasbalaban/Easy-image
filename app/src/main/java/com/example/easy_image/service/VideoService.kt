package com.example.easy_image.service

import com.example.easy_image.model.ImageResponse
import com.example.easy_image.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {
    @GET("api/videos")
    suspend fun getVideos(
        @Query("key") key: String,
        @Query("q") query: String?,
        @Query("page") page: Int
    ) : VideoResponse?
}