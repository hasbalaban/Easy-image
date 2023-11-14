package com.example.easy_image.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easyImage.mediapi.model.VideoItemDTO
import com.easyImage.mediapi.repository.video.VideoRepository
import com.easyImage.mediapi.utils.Resource
import com.example.easy_image.utils.ignoreNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepository : VideoRepository
) : MainViewModel() {

    private val _videos = MutableLiveData<Resource<List<VideoItemDTO>?>>()
    val videos : LiveData<Resource<List<VideoItemDTO>?>> get() = _videos
    private var currentImageRequestPage = 1

    fun getVideos(
        query: String = "roket",
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

        try {
            viewModelScope.launch {
                val videos = videoRepository.getVideos(
                    time = 0,
                    key = "39342921-c040c554a9e966b3202b73519",
                    query = query,
                    page = currentImageRequestPage
                )

                videos.collect{
                    _videos.value = when(it.status){
                        Resource.Status.SUCCESS -> {
                            val videoItemDto = it.data?.hits?.mapIndexed { index, videoItem ->

                                VideoItemDTO(
                                    id = videoItem.id,
                                    videoPreviewUrl = videoItem.videos?.small?.url ?: "",
                                    videoUrl = videoItem.videos?.large?.url ?: "",
                                    isVideoPlaying = index == 0 && _videos.value == null,
                                    isMusicOpen = index == 0 && _videos.value == null,
                                    videoTag =videoItem.tags ?: ""
                                )
                            }
                            _videos.value?.data?.let {
                                val newList = it +  videoItemDto
                                Resource.success(newList)
                            }?: run{
                                Resource.success(videoItemDto)
                            }

                            currentImageRequestPage += 1

                            Resource.success(videoItemDto)
                        }
                        Resource.Status.ERROR -> Resource.error("error", null)
                        Resource.Status.LOADING -> Resource.loading(null)
                        Resource.Status.RESET -> Resource.reset()
                    }
                }

            }
        }catch (e : Exception){
            println(e.cause)
        }
    }

    fun videoMusicStatusChanged(videoId : Int){
        val newVideoList = _videos.value?.data?.map {
            val isMusicOpen: Boolean =
                if (it.id == videoId)
                    it.isMusicOpen.not()
                else
                    false

            VideoItemDTO(
                id = it.id.ignoreNull(),
                videoPreviewUrl = it.videoPreviewUrl,
                videoUrl = it.videoUrl,
                isVideoPlaying = it.isVideoPlaying,
                isMusicOpen = isMusicOpen,
                videoTag = it.videoTag,
            )
        }
        //_videos.value = null
        _videos.value = Resource.Companion.success(newVideoList)
    }
    fun videoVideoPlayingStatusChanged(videoId : Int){
        val newVideoList = _videos.value?.data?.map {
            val isVideoPlaying: Boolean =
                if (it.id == videoId)
                    it.isVideoPlaying.not()
                else
                    false

            VideoItemDTO(
                id = it.id.ignoreNull(),
                videoPreviewUrl = it.videoPreviewUrl,
                videoUrl = it.videoUrl,
                isVideoPlaying = isVideoPlaying,
                isMusicOpen = it.isMusicOpen,
                videoTag = it.videoTag,
            )
        }
        //_videos.value = null
        _videos.value = Resource.Companion.success(newVideoList)
    }
    fun videoAutoPlayingStatusChanged(videoId : Int){
        if (_videos.value?.data?.firstOrNull { it.id == videoId }?.isVideoPlaying == true) return

        val newVideoList = _videos.value?.data?.map {
            val isVideoPlaying: Boolean =
                it.id == videoId

            VideoItemDTO(
                id = it.id.ignoreNull(),
                videoPreviewUrl = it.videoPreviewUrl,
                videoUrl = it.videoUrl,
                isVideoPlaying = isVideoPlaying,
                isMusicOpen = it.isMusicOpen,
                videoTag = it.videoTag,
            )
        }
        _videos.value = Resource.Companion.success(newVideoList)
    }
}