package com.easyImage.mediapi.repository.video

import com.easyImage.mediapi.model.VideoResponse
import com.easyImage.mediapi.utils.Resource
import com.easyImage.mediapi.utils.handleResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val localVideoDataSource: LocalVideoDataSource,
    private val remoteVideoDataSource: RemoteVideoDataSource,
) {
    suspend fun getVideos(
        time: Long,
        key : String,
        query : String,
        page : Int

    ): Flow<Resource<VideoResponse?>> {
        return if (time > 10) remoteVideoDataSource.getVideos(
                key = key,
                query = query,
                page = page
        ).handleResponse()
        else localVideoDataSource.getVideos(
            key = key,
            query = query,
            page = page
        ).handleResponse()
    }
}