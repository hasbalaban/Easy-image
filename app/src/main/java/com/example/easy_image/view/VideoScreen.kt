package com.example.easy_image.view

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.easy_image.R
import com.example.easy_image.viewmodel.VideoViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val mContext = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getVideos()
    }

    val videos = viewModel.videos.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        val dataSourceFactory = DefaultDataSourceFactory(
            mContext,
            Util.getUserAgent(mContext, mContext.packageName)
        )


        videos.value?.hits?.let {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(it) {
                    Column(modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()) {

                        val videoURL =it.videos?.medium?.url
                        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(videoURL))
                        val mExoPlayer = remember(mContext) {
                            ExoPlayer.Builder(mContext).build().apply {
                                prepare(source)
                                play()
                                repeatMode = REPEAT_MODE_ONE
                            }
                        }
                        AndroidView(modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp), factory = { context ->

                            PlayerView(context).apply {
                                player = mExoPlayer
                                controllerShowTimeoutMs = 1000
                                defaultArtwork = resources.getDrawable(R.drawable.ic_home)
                                useController = false
                            }
                        })
                    }

                }
            }
        }


    }
}