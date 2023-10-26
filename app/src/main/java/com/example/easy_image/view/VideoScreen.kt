package com.example.easy_image.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@Composable
fun VideoScreen (navController: NavController){
    val mContext = LocalContext.current
    val videoURL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    val mExoPlayer = remember(mContext) {
        ExoPlayer.Builder(mContext).build().apply {

            val dataSourceFactory = DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mContext.packageName))
            val source = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoURL))
            prepare(source)
        }
    }
    // Implementing ExoPlayer

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Red),
        verticalArrangement = Arrangement.Center
    ) {

        AndroidView(factory = { context ->
            PlayerView(context).apply {
                player = mExoPlayer
                controllerShowTimeoutMs = 2000
            }
        })
    }
}