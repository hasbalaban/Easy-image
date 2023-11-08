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
                            val videoItemDto = it.data?.hits?.filterIndexed { index, videoItem ->
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
                it.id.ignoreNull(),
                it.videoPreviewUrl,
                it.videoUrl,
                isMusicOpen,
                it.videoTag,
            )
        }
        _videos.value = null
        _videos.value = Resource.Companion.success(newVideoList)
    }
}