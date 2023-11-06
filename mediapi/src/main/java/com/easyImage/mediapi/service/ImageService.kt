package com.easyImage.mediapi.service

import com.easyImage.mediapi.model.ImageResponse
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