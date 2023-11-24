package com.example.easy_image

import com.easyImage.mediapi.utils.Resource
import com.example.easy_image.repository.FakeRemoteVideoDataSource
import com.example.easy_image.repository.FakeVideoRepositoryImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class FakeVideoRepositoryTest {
    @Test
    fun check_here() {
        val remote = FakeRemoteVideoDataSource()
        val repository = FakeVideoRepositoryImpl(remote)
        runBlocking {
            val a = repository.getVideos(1L, "1", "dd", 1)
            a.collect{
                Assert.assertEquals(it.status, Resource.Status.SUCCESS)
            }
            Assert.assertEquals(4, 2 + 2)
        }
    }
}