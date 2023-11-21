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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.easy_image.utils.ExoPlayerManager
import com.example.easy_image.viewmodel.VideoViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoPlayScreen (
    viewModel: VideoViewModel = hiltViewModel()
) {
    val list = viewModel.videos.observeAsState()
    val context = LocalContext.current
    val state = rememberLazyListState()


    var windowHeight by remember {
        mutableStateOf(0.dp)
    }
    windowHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit){
        viewModel.getVideos("car", true)
    }

    val coroutines = rememberCoroutineScope()

    val items = list.value?.data

    items?.let {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = state),

                ){
                items(it){
                    val exoPlayer by remember {
                        mutableStateOf(
                            ExoPlayerManager.initializePlayer(context, it.videoUrl).also {
                                it.prepare()
                                it.play()
                            }
                        )
                    }


                    Box (modifier = Modifier.height(windowHeight)){
                        Box(modifier = Modifier.fillMaxSize()
                            .alpha(0.8f)
                            .background(Color.Black)){

                        }

                        Column (modifier = Modifier.height(windowHeight), verticalArrangement = Arrangement.Center){
                            VideoItemScreen(it, null, null, exoPlayer = exoPlayer){
                                coroutines.launch {
                                    if (items.size > state.firstVisibleItemIndex + 1){
                                        state.apply {
                                            animateScrollBy(1f, spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = Spring.StiffnessHigh,
                                                null
                                            ))
                                            animateScrollToItem(state.firstVisibleItemIndex + 1)
                                        }
                                    }
                                }
                            }
                            Text(text = it.videoTag)
                        }



                    }

                }
            }
    }
}