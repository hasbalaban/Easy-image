package com.example.easy_image.repository

import com.easyImage.mediapi.hiltmodules.AppConstants
import com.easyImage.mediapi.model.ImageDTO
import com.easyImage.mediapi.model.VideoResponse
import com.easyImage.mediapi.repository.video.VideoDataOperation
import com.easyImage.mediapi.service.ImageService
import com.easyImage.mediapi.service.VideoService
import com.easyImage.mediapi.utils.WrapResponse
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class FakeRemoteVideoDataSource(

) : VideoDataOperation {
    override suspend fun getVideos(
        key: String,
        query: String?,
        page: Int
    ): Response<WrapResponse<VideoResponse>> {
        val gson = GsonBuilder().create()

        val videoService =  Retrofit.Builder()
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

    suspend fun getPhotos(
        query: String = "planet",
        shouldClearPhotos : Boolean = false,
        currentImageRequestPage : Int,
    ){
        var queryText = query


        if (queryText.isEmpty()){
            queryText = "planet"
        }

        val photoService: ImageService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageService::class.java)

        try {
            val photos = photoService.getPhotos(
                key = "36463103-c2d65a399fefc8955088325ab",
                query = queryText,
                page = currentImageRequestPage
            )
            photos?.let {
                it.hits?.let { newList ->

                    val lastList = newList.map{item ->
                        ImageDTO(
                            id = item.id,
                            previewURL = item.previewURL,
                            largeImageURL = item.largeImageURL,
                            fullHDURL = item.fullHDURL,
                            imageURL = item.imageURL,
                        )
                    }

                    lastList

                }
            }
        }catch (e : Exception){
            println(e.cause)
        }
    }
}