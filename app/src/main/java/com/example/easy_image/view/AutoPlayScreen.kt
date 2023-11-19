package com.example.easy_image.view

import android.content.Context.WINDOW_SERVICE
import android.view.WindowManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easyImage.mediapi.model.VideoItemDTO
import com.example.easy_image.utils.ExoPlayerManager
import com.example.easy_image.utils.ignoreNull
import com.example.easy_image.viewmodel.VideoViewModel


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



        list.value?.data?.let {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = state),

                ){
                items(it){
                    val exoPlayer by remember {
                        mutableStateOf(
                            ExoPlayerManager.initializePlayer(context, it.videoUrl).also {

                                //  it.prepare()
                                //   it.play()
                            }
                        )
                    }
                    Column (modifier = Modifier.height(windowHeight), verticalArrangement = Arrangement.Center){
                        VideoItemScreen(it, null, null, exoPlayer = exoPlayer)
                        Text(text = it.videoTag)
                    }

                }
            }
    }
}