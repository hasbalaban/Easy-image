package com.example.easy_image.view

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import androidx.navigation.NavController
import com.example.easy_image.R
import com.easyImage.mediapi.model.VideoItemDTO
import com.easyImage.mediapi.utils.Resource
import com.example.easy_image.utils.ExoPlayerManager
import com.example.easy_image.viewmodel.VideoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoScreen(
    navController: NavController,
    openVideoDetail: (String) -> Unit,
    searchMediaText : String?,
    viewModel: VideoViewModel = hiltViewModel()
) {
    // val configuration = LocalConfiguration.current
    //  val screenHeightDp = configuration.screenHeightDp.dp
    LaunchedEffect(Unit) {
        viewModel.getVideos(searchMediaText)
    }

    val videosResult = viewModel.videos.observeAsState()
    val videoList by remember { derivedStateOf {videosResult.value?.data } }
    val listState by remember { mutableStateOf(LazyListState()) }

    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()




    videoList?.let{
        if (it.isEmpty()) return@let
        if (!listState.isScrollInProgress) return@let


        val fullyVisibleItemsInfo = listState.layoutInfo.visibleItemsInfo.toMutableList()

        val viewportHeight =
            listState.layoutInfo.viewportEndOffset + listState.layoutInfo.viewportStartOffset
        val lastItem = fullyVisibleItemsInfo.last()
        val firstItemIfLeft = fullyVisibleItemsInfo.firstOrNull()


        if (lastItem.offset + lastItem.size > viewportHeight) {
            fullyVisibleItemsInfo.removeLast()
        }
        if (firstItemIfLeft != null && firstItemIfLeft.offset < listState.layoutInfo.viewportStartOffset) {
            fullyVisibleItemsInfo.removeFirst()
        }

        val playingVideoIndex = if (fullyVisibleItemsInfo.last().index == it.size - 1)
            fullyVisibleItemsInfo.last().index
        else
            fullyVisibleItemsInfo.firstOrNull()?.index ?: return@let

        coroutine.launch {
            val playingVideoITem = it[playingVideoIndex]

            if (it.firstOrNull { it.id == playingVideoITem.id }?.isVideoPlaying == true) return@launch

            viewModel.videoAutoPlayingStatusChanged(playingVideoITem.id)
        }
    }





when (videosResult.value?.status){
    Resource.Status.SUCCESS -> {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            videoList?.let {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    items(it,
                        key = {
                            it.id
                        }
                    ) {

                        var fraction by remember { mutableStateOf(0.0f) }

                        val url = it.videoPreviewUrl.ifEmpty { it.videoUrl }
                        val exoPlayer by remember {
                            mutableStateOf(
                                ExoPlayerManager.initializePlayer(context, url)
                            )
                        }


                    LaunchedEffect(it.isVideoPlaying){
                        exoPlayer.volume = if (it.isMusicOpen) 1f else 0f
                        if (it.isVideoPlaying){
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }else {
                            exoPlayer.pause()
                        }


                        while (it.isVideoPlaying) {
                            delay(800)
                            if (!exoPlayer.isPlaying) continue
                            val duration = (exoPlayer.duration / 1000).toFloat()
                            val currentPosition = (exoPlayer.currentPosition / 1000).toFloat()
                            val percent = (currentPosition / duration)

                            if (percent > 0f) {
                                fraction = percent
                            }
                        }
                    }

                        LaunchedEffect(it.isMusicOpen){
                            exoPlayer.volume = if (it.isMusicOpen) 1f else 0f
                        }


                        Column(modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()) {

                            Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd){
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .height(230.dp),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    VideoItemScreen(it, openVideoDetail, viewModel, exoPlayer = exoPlayer)

                                    val imageIcon = if (it.isMusicOpen) R.drawable.music_on else R.drawable.music_off
                                    Image(
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.videoMusicStatusChanged(it.id)
                                            }
                                            .padding(12.dp),
                                        painter = painterResource(id = imageIcon), contentDescription = "sound status" )

                                }
                                val modifier = Modifier

                                Column(
                                    modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                        .animateContentSize()
                                        .then(
                                            if (it.isVideoPlaying) modifier.wrapContentSize() else modifier.height(
                                                0.dp
                                            )
                                        )) {

                                    VideoTimeLineBar(fraction = fraction, Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)) {
                                        val newValue = exoPlayer.duration * it
                                        exoPlayer.seekTo(newValue.toLong())
                                    }
                                }

                            }

                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, top = 2.dp),
                                text = it.videoTag,
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                    }
                }
            }

        }


    }
    Resource.Status.ERROR -> "TODO()"
    Resource.Status.LOADING -> "TODO()"
    Resource.Status.RESET -> "TODO()"
    null -> ""
}
}
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoItemScreen(
    videoItemDTO: VideoItemDTO,
    openVideoDetail: (String) -> Unit,
    viewModel: VideoViewModel,
    exoPlayer: ExoPlayer
) {
    val context = LocalContext.current

    DisposableEffect(AndroidView(modifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    val url = videoItemDTO.videoUrl.ifEmpty { videoItemDTO.videoPreviewUrl }

                    openVideoDetail(url)
                },
                onTap = {
                    viewModel.videoVideoPlayingStatusChanged(videoItemDTO.id)
                }
            )

        }
        .fillMaxWidth()
        .height(230.dp), factory = {
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


    if (videoItemDTO.isVideoPlaying.not()) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(95.dp),
            painter = painterResource(id = R.drawable.ic_video), contentDescription = null
        )
    }


}

