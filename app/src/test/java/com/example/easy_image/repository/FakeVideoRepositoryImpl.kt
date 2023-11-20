package com.example.easy_image.repository

import com.easyImage.mediapi.model.VideoItemDTO
import com.easyImage.mediapi.model.VideoResponse
import com.easyImage.mediapi.repository.video.VideoRepositoryOperations
import com.easyImage.mediapi.repository.video.VideoRepositoryOperations1
import com.easyImage.mediapi.utils.Resource
import com.easyImage.mediapi.utils.handleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeVideoRepositoryImpl @Inject constructor(
    private val remoteVideoDataSource: FakeRemoteVideoDataSource
) : VideoRepositoryOperations1 {
    override suspend fun getVideos(
        time: Long,
        key: String,
        query: String,
        page: Int

    ): Flow<Resource<List<VideoItemDTO>?>> = flow {
        emit(Resource.loading(null))

    }
}
