package com.example.easy_image.view

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.media3.common.C
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import androidx.navigation.NavController
import com.example.easy_image.R
import com.example.easy_image.model.VideoItemDTO
import com.example.easy_image.utils.ExoPlayerManager
import com.example.easy_image.viewmodel.VideoViewModel


@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val scrollState = remember { LazyListState() }
    val playedVideo = remember {
        derivedStateOf {
            ((scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: 0) + (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)) / 2
        }
    }


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

       // val configuration = LocalConfiguration.current
      //  val screenHeightDp = configuration.screenHeightDp.dp

      //  val context = LocalContext.current
        videos.value?.let {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(it,
                    key = {
                        it.id
                    }
                ) {

                    Column(modifier = Modifier
                        .clickable {
                            viewModel.videoMusicStatusChanged(it.id)
                        }
                        .padding(top = 20.dp)
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
        mutableStateOf(
            ExoPlayerManager.initializePlayer(context).apply {
                volume = if (videoItemDTO.isMusicOpen) 1f else 0f
                setHandleAudioBecomingNoisy(true)
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

            }
        )
    }

    val playerView = remember {
        PlayerView(context).apply {
            player = exoPlayer.apply {
                setShowBuffering(SHOW_BUFFERING_ALWAYS)
            }
            controllerShowTimeoutMs = 10
            useController = false
        }
    }



    DisposableEffect(key1 = true) {
        onDispose {
            playerView.player = null
            ExoPlayerManager.releasePlayer(exoPlayer = exoPlayer, videoItemDTO)
        }
    }


    if (videoItemDTO.isMusicOpen){
        ExoPlayerManager.setMediaItem(
            exoPlayer = exoPlayer,
            videoUri = videoItemDTO.videoUrl,
            playbackPosition = videoItemDTO.playbackPosition
        )
    }else {
        exoPlayer.stop()
    }


    AndroidView(modifier = Modifier
        .fillMaxWidth()
        .height(240.dp), factory = { playerView }
    )
    if (videoItemDTO.isMusicOpen.not()){
        Image(
            modifier = Modifier.fillMaxWidth().height(240.dp).padding(100.dp),
            painter = painterResource(id = R.drawable.ic_video), contentDescription = null)
    }
}

