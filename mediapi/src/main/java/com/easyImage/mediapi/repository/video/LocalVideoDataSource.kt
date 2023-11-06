package com.easyImage.mediapi.repository.video

import javax.inject.Inject

class LocalVideoDataSource @Inject constructor(

): VideoDataOperation {
    override suspend fun getVideos(): Int {
        return 1
    }
}