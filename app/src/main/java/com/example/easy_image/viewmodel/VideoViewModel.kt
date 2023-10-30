package com.example.easy_image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.easy_image.model.VideoItem
import com.example.easy_image.model.VideoResponse
import com.example.easy_image.service.VideoService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoViewModel : MainViewModel() {

    private val _videos = MutableLiveData<VideoResponse?>()
    val videos : LiveData<VideoResponse?> get() = _videos
    private var currentImageRequestPage = 1

    fun getVideos(
        query: String = "planet",
        shouldClearPhotos : Boolean = false
    ){
        var queryText = query
        if (shouldClearPhotos) {
            _videos.value = null
            currentImageRequestPage = 1
        }

        if (queryText.isEmpty()){
            queryText = "planet"
            currentImageRequestPage = 1
        }

        val videoService: VideoService = Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VideoService::class.java)

        try {
            viewModelScope.launch {
                val videos = videoService.getVideos(
                    key = "39342921-c040c554a9e966b3202b73519",
                    query = "river",
                    page = currentImageRequestPage
                )
                videos?.let {
                    val images: List<VideoItem>? = it.hits?.let { newList ->

                        println(newList)

                        (_videos.value?.hits ?: mutableListOf()).plus(newList)
                    }


                    _videos.value = it.copy(
                        hits = images
                    )
                    currentImageRequestPage += 1
                }
            }
        }catch (e : Exception){
            println(e.cause)
        }
    }

    fun videoMusicStatusChanged(videoId : Int){
        _videos.value?.hits?.let {
            for (i in it)
                if (i.id == videoId) {
                    i.isMusicOpen = i.isMusicOpen.not()
                    _videos.value = _videos.value
                    break
                }
        }

    }
}