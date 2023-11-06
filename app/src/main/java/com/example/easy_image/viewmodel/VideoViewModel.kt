package com.example.easy_image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easyImage.mediapi.model.VideoItemDTO
import com.easyImage.mediapi.service.VideoService
import com.example.easy_image.utils.ignoreNull
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoViewModel : MainViewModel() {

    private val _videos = MutableLiveData<List<VideoItemDTO>?>()
    val videos : LiveData<List<VideoItemDTO>?> get() = _videos
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
                    query = "train",
                    page = currentImageRequestPage
                )
                val videoList = videos?.hits?.filterIndexed { index, videoItem ->
                    index != 0
                }?.mapIndexed { index, videoItem ->

                    VideoItemDTO(
                        videoItem.id,
                        videoItem.videos?.medium?.url ?: "",
                        videoItem.videos?.large?.url ?: "",
                        index == 0 && _videos.value == null,
                        videoItem.tags ?: "",
                    )
                }

                _videos.value = videoList?.plus(_videos.value ?: listOf())
                currentImageRequestPage += 1
            }
        }catch (e : Exception){
            println(e.cause)
        }
    }

    fun videoMusicStatusChanged(videoId : Int){


        val newVideoList = _videos.value?.map {
            val isMusicOpen: Boolean =
                if (it.id == videoId)
                    it.isMusicOpen.not()
                else
                    false

            VideoItemDTO(
                it.id.ignoreNull(),
                it.videoPreviewUrl,
                it.videoUrl,
                isMusicOpen,
                it.videoTag,
            )
        }
        _videos.value = null
        _videos.value = newVideoList

    }
}