package com.example.easy_image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {
    private val _photos = MutableLiveData<ImageResponse?>()
    val photos : LiveData<ImageResponse?> get() = _photos
    private var currentImageRequestPage = 1

    fun updatePhotos(photos: ImageResponse?){
        _photos.value = photos
    }

    fun getPhotos(
        query: String = "Sun",
        shouldClearPhotos : Boolean = false
    ){
        var queryText = query
        if (shouldClearPhotos) {
            _photos.value = null
            currentImageRequestPage = 1
        }

        if (queryText.isEmpty()){
            queryText = "sun"
            currentImageRequestPage = 1
        }

        val photoService: PhotoApiService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotoApiService::class.java)

        try {
            viewModelScope.launch {
                val photos = photoService.getPhotos(
                    key = "39342921-c040c554a9e966b3202b73519",
                    query = queryText,
                    page = currentImageRequestPage
                )
                photos?.let {
                    val images: List<Hits>? = it.hits?.let { newList ->

                        newList.forEach{item ->
                        }

                        (_photos.value?.hits ?: mutableListOf()).plus(newList)
                    }

                    images?.forEach {item ->
                        item.isFavorite = SavedImages.savedImages.any { it.id == item.id }
                    }

                    _photos.value = it.copy(
                        hits = images
                    )
                    currentImageRequestPage += 1
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