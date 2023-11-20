package com.example.easy_image.hilt

import com.easyImage.mediapi.hiltmodules.AppConstants
import com.easyImage.mediapi.service.VideoService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

