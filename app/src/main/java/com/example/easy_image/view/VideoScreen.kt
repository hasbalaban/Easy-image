package com.example.easy_image.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    openVideoDetail: (String) -> Unit,
    viewModel: VideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getVideos()
    }

    val videos = viewModel.videos.observeAsState()

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
                        .padding(top = 20.dp)
                        .fillMaxWidth()) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .height(240.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            VideoItemScreen(it, openVideoDetail, viewModel)

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
fun VideoItemScreen(
    videoItemDTO: VideoItemDTO,
    openVideoDetail: (String) -> Unit,
    viewModel: VideoViewModel
) {
    val context = LocalContext.current



    val exoPlayer by remember {
        mutableStateOf(
            ExoPlayerManager.initializePlayer(context)
        )
    }

    DisposableEffect(AndroidView(modifier = Modifier
        .pointerInput(Unit){
            detectTapGestures(
                onDoubleTap = {
                    openVideoDetail(videoItemDTO.videoUrl)
                },
                onTap = {
                    viewModel.videoMusicStatusChanged(videoItemDTO.id)
                }
            )

        }
        .fillMaxWidth()
        .height(240.dp), factory = {
        PlayerView(context).apply {
            player = exoPlayer.apply {
                setShowBuffering(SHOW_BUFFERING_ALWAYS)
                useController = false

            }
        }
    }
    )) {
        onDispose {
            ExoPlayerManager.releasePlayer(exoPlayer = exoPlayer)
        }
    }

    LaunchedEffect(videoItemDTO.isMusicOpen){
        exoPlayer.volume = if (videoItemDTO.isMusicOpen) 1f else 0f
        if (videoItemDTO.isMusicOpen){
            ExoPlayerManager.setMediaItem(
                exoPlayer = exoPlayer,
                videoUri = videoItemDTO.videoPreviewUrl,
            )
        }else {
            exoPlayer.stop()
        }
    }

    DisposableEffect(
        if (videoItemDTO.isMusicOpen.not()) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(100.dp),
                painter = painterResource(id = R.drawable.ic_video), contentDescription = null
            )
        } else {

        }
    ) {
        onDispose { }

    }


}

