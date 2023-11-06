package com.easyImage.mediapi.repository.video

import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val localVideoDataSource: LocalVideoDataSource,
    private val remoteVideoDataSource: RemoteVideoDataSource,
) {
    suspend fun getVideos(
        time: Long
    ): Int {
        return if (time > 10) remoteVideoDataSource.getVideos()
        else localVideoDataSource.getVideos()
    }
}