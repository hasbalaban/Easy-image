package com.example.easy_image.view

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.easy_image.R
import com.example.easy_image.model.VideoItemDTO
import com.example.easy_image.viewmodel.VideoViewModel


@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.getVideos()
    }

    val videos = viewModel.videos.observeAsState()

    if (videos.value == null){
        println("")
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        val context = LocalContext.current

        videos.value?.let {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(it,
                    key = {
                        it.id
                    }
                ) {
                    val videoURL =it.videoUrl


                    val isMusicOpen = it.isMusicOpen

                    Column(modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .height(240.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            VideoItemScreen(it)

                            val imageIcon = if (it.isMusicOpen) R.drawable.music_on else R.drawable.music_off
                            Image(
                                modifier = Modifier
                                    .clickable {

                                        viewModel.videoMusicStatusChanged(it.id)
                                    }
                                    .padding(12.dp),
                                painter = painterResource(id = imageIcon), contentDescription = "sound status" )

                        }
                    }

                }
            }
        }
    }
}
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoItemScreen(videoItemDTO: VideoItemDTO) {
    val context = LocalContext.current

    val exoPlayer by remember {
        val mediaItem = MediaItem.fromUri(Uri.parse(videoItemDTO.videoUrl))
        mutableStateOf(
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(mediaItem)
                repeatMode = REPEAT_MODE_ONE
                this.volume = 1f
            }
        )
    }

    exoPlayer.addListener(object : Player.Listener{
        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) { }else { }
        }

        override fun onPlayerError(error: PlaybackException) {
            val cause = error.cause
            if (cause is HttpDataSource.HttpDataSourceException) {

                if (cause is HttpDataSource.InvalidResponseCodeException) { } else { }
            }
        }
    })





    if (videoItemDTO.isMusicOpen){
        exoPlayer.prepare()
        exoPlayer.play()
    }else {
        exoPlayer.stop()

    }


    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(240.dp), factory = { _ ->
        PlayerView(context).apply {
            player = exoPlayer.apply {
                if (videoItemDTO.isMusicOpen) {
                    prepare()
                    play()
                } else {
                    stop()
                }
            }
            controllerShowTimeoutMs = 1000
            useController = false
        }
    })
}

