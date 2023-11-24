package com.easyImage.mediapi.repository.video

import com.easyImage.mediapi.model.VideoItemDTO
import com.easyImage.mediapi.utils.Resource
import com.easyImage.mediapi.utils.handleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class VideoRepositoryImpl @Inject constructor(
    private val remoteVideoDataSource: RemoteVideoDataSource,
) : VideoRepositoryOperations {
    override suspend fun getVideos(
        time: Long,
        key : String,
        query : String,
        page : Int
    ): Flow<Resource<List<VideoItemDTO>?>> = flow {
            val videos = remoteVideoDataSource.getVideos(
                key = "39342921-c040c554a9e966b3202b73519",
                query = query,
                page = page
            )

            videos.handleResponse().collect{
                when(it.status){
                    Resource.Status.SUCCESS -> {
                        val videoItemDto = it.data?.hits?.mapIndexed { index, videoItem ->

                            VideoItemDTO(
                                id = videoItem.id,
                                videoPreviewUrl = videoItem.videos?.small?.url ?: "",
                                videoUrl = videoItem.videos?.large?.url ?: "",
                                isVideoPlaying = false,
                                isMusicOpen = true,
                                videoTag =videoItem.tags ?: ""
                            )
                        }


                        emit(Resource.success(videoItemDto))
                    }
                    Resource.Status.ERROR -> emit(Resource.error("error", null))
                    Resource.Status.LOADING -> emit(Resource.loading(null))
                    Resource.Status.RESET ->emit( Resource.reset())
                }
            }


        }


}

interface VideoRepositoryOperations{
    suspend fun getVideos(
        time: Long,
        key : String,
        query : String,
        page : Int

    ): Flow<Resource<List<VideoItemDTO>?>>

}