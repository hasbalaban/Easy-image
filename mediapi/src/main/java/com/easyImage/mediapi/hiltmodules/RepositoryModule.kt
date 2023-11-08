package com.easyImage.mediapi.hiltmodules

import com.easyImage.mediapi.repository.video.LocalVideoDataSource
import com.easyImage.mediapi.repository.video.RemoteVideoDataSource
import com.easyImage.mediapi.repository.video.VideoRepository
import com.easyImage.mediapi.service.VideoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoService: VideoService
    ) : VideoRepository {
        return VideoRepository(
            localVideoDataSource = LocalVideoDataSource(videoService),
            remoteVideoDataSource = RemoteVideoDataSource(videoService),
        )
    }

  /*
    @Provides
    @Singleton
    fun provideImageRepository(
        videoService: VideoService
    ) : VideoRepository {
        return VideoRepository(
            localVideoDataSource = LocalVideoDataSource(videoService),
            remoteVideoDataSource = RemoteVideoDataSource(videoService),
        )
    }
   */
}