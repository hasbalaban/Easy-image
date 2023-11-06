package com.easyImage.mediapi.repository.video

import javax.inject.Inject

class RemoteVideoDataSource @Inject constructor(

) : VideoDataOperation {
    override suspend fun getVideos(): Int {
        return 2
    }
}