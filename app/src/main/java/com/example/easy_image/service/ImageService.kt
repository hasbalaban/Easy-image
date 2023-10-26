package com.example.easy_image.service

import com.example.easy_image.model.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {
    @GET("api")
    suspend fun getPhotos(
        @Query("key") key: String,
        @Query("q") query: String?,
        @Query("page") page: Int
    ) : ImageResponse?
}