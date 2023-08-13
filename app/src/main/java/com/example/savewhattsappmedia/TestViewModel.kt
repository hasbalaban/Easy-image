package com.example.savewhattsappmedia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.Exception


class TestViewModel : ViewModel() {
    private val _photos = MutableLiveData<ImageResponse?>()
    val photos : LiveData<ImageResponse?> get() = _photos

    fun getPhotos(
        query: String?,
        page: Int
    ){
        val photoService: PhotoApiService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoApiService::class.java)

        try {
            viewModelScope.launch {
                val photos = photoService.getPhotos(
                    key = "36463103-c2d65a399fefc8955088325ab",
                    query = query,
                    page = page
                )
                photos?.let {


                    val images: List<Hits>? = it.hits?.let { newList ->
                        (_photos.value?.hits ?: mutableListOf()).plus(newList)
                    }

                    _photos.value = it.copy(
                        hits = images
                    )
                }
            }
        }catch (e : Exception){
            println(e.cause)
        }
    }
}

interface PhotoApiService {
    @GET("api")
    suspend fun getPhotos(
        @Query("key") key: String,
        @Query("q") query: String?,
        @Query("page") page: Int
    ) : ImageResponse?
}