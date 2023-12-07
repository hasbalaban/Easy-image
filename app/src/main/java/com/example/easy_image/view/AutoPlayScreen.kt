package com.example.easy_image.view

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.media.mediaPlayer.addMediaItem
import com.android.media.mediaPlayer.rememberMediaPlayer
import com.android.media.mediaPlayer.startVideo
import com.example.easy_image.viewmodel.VideoViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoPlayScreen (
    viewModel: VideoViewModel = hiltViewModel()
) {
    val list = viewModel.videos.observeAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()


    var windowHeight by remember {
        mutableStateOf(0.dp)
    }
    windowHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit){
        viewModel.getVideos("roket", true)
    }

    val coroutines = rememberCoroutineScope()

    val items = list.value?.data

    val fullyVisibleItemsInfo = remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.toMutableList() } }


    fullyVisibleItemsInfo.value.firstOrNull()?.let {
        items?.get(it.index)?.let {
            viewModel.videoAutoPlayingStatusChanged(it.id)
        }
    }


    items?.let {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),

                ){
                items(it){
                    val exoPlayer = rememberMediaPlayer()
                    LaunchedEffect(exoPlayer){
                        exoPlayer.addMediaItem(it.videoPreviewUrl).startVideo()
                    }

                    LaunchedEffect(it.isVideoPlaying){
                        if (it.isVideoPlaying) {
                            exoPlayer.play()
                            return@LaunchedEffect
                        }
                        exoPlayer.pause()
                    }

                    Box (modifier = Modifier.height(windowHeight)){
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.8f)
                            .background(Color.Black)){

                        }

                        Column (modifier = Modifier.height(windowHeight), verticalArrangement = Arrangement.Center){
                            VideoItemScreen(it, null, null, exoPlayer = exoPlayer){

                                coroutines.launch {
                                    if (items.size > listState.firstVisibleItemIndex + 1){
                                        listState.apply {
                                            animateScrollBy(1f, spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = Spring.StiffnessHigh,
                                                null
                                            ))
                                            animateScrollToItem(listState.firstVisibleItemIndex + 1)
                                            viewModel.videoAutoPlayingStatusChanged(items[listState.firstVisibleItemIndex + 1].id)
                                        }
                                    }
                                }
                            }
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 12.dp), text = it.videoTag, color = Color.White, textAlign = TextAlign.End)
                        }



                    }

                }
            }
    }
}