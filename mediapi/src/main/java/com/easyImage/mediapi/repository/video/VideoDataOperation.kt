package com.easyImage.mediapi.repository.video

import kotlinx.coroutines.flow.Flow

interface VideoDataOperation {

    suspend fun getVideos() : Int

}