package com.example.easy_image.viewmodel

import com.easyImage.mediapi.service.VideoService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named

/*
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {


    @Provides
    @Named("sfsdds")
    fun provideAnalyticsService(
        // Potential dependencies of this type
    ): VideoService {
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(VideoService::class.java)
    }
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface LoginModuleDependencies {


}


 */