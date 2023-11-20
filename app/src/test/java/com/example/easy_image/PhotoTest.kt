package com.example.easy_image

import com.example.easy_image.repository.FakeRemoteVideoDataSource
import com.example.easy_image.repository.FakeVideoRepositoryImpl
import com.example.easy_image.viewmodel.VideoViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class PhotoTest {
    private lateinit var videoRepository : FakeVideoRepositoryImpl
    private lateinit var viewModel : VideoViewModel

    @Before
    fun setUp(){
        videoRepository = FakeVideoRepositoryImpl(
            remoteVideoDataSource = FakeRemoteVideoDataSource()
        )

    }

    @Test
    fun aaa (){
        runBlocking {
            val item = videoRepository.getVideos(
                1L,
                "",
                "",
                1
            )

         //   assert(item).

        }
    }




}