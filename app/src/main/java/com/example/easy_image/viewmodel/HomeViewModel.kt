package com.example.easy_image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easy_image.model.Hits
import com.example.easy_image.model.ImageResponse
import com.example.easy_image.service.ImageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class HomeViewModel @Inject constructor(): MainViewModel() {
    private val _photos = MutableLiveData<ImageResponse?>()
    val photos : LiveData<ImageResponse?> get() = _photos
    private var currentImageRequestPage = 1

    fun getPhotos(
        query: String = "planet",
        shouldClearPhotos : Boolean = false
    ){
        var queryText = query
        if (shouldClearPhotos) {
            _photos.value = null
            currentImageRequestPage = 1
        }

        if (queryText.isEmpty()){
            queryText = "planet"
            currentImageRequestPage = 1
        }

        val photoService: ImageService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageService::class.java)

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