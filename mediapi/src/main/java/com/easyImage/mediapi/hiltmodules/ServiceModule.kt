package com.easyImage.mediapi.hiltmodules

import com.easyImage.mediapi.service.ImageService
import com.easyImage.mediapi.service.VideoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideVideoService(@Named("video_api") retrofit: Retrofit): VideoService =
        retrofit.create(VideoService::class.java)

    @Provides
    @Singleton
    fun provideImageService(@Named("image_api") retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)
}