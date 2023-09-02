package com.example.savewhattsappmedia.view

import android.content.Context
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savewhattsappmedia.ImageResponse
import com.example.savewhattsappmedia.TestViewModel

@Composable
fun HomeScreen(openImageDetail: (String) -> Unit, viewModel: TestViewModel= androidx.lifecycle.viewmodel.compose.viewModel())  {

    LaunchedEffect(Unit) {
        viewModel.getPhotos()
    }

    val photos = viewModel.photos.observeAsState()

    val state = rememberLazyGridState()
    val shouldGetNewPage by remember {
        derivedStateOf {
            (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: 0) >= ((photos.value?.hits?.size ?: 1) * 0.8)
        }
    }


    ObserveCounter(photos.value, shouldGetNewPage, state, openImageDetail){
        viewModel.getPhotos()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObserveCounter(
    photos: ImageResponse?,
    shouldGetNewPage: Boolean,
    state: LazyGridState,
    openImageDetail : (String) -> Unit,
    scrollToBottomOfPhotos : () -> Unit
) {
    SideEffect {
        if (shouldGetNewPage) {
            scrollToBottomOfPhotos.invoke()
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { MainTopAppBar(scrollBehavior)}
    ) {
        Column(modifier = Modifier.padding(it)) {
            MainContent(photos, state, openImageDetail)
        }
    }

}


@Composable
private fun MainContent(
    photos: ImageResponse?,
    state: LazyGridState,
    openImageDetail: (String) -> Unit
) {

    photos?.hits?.let {
        val context = LocalContext.current

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
            userScrollEnabled = true,
            state = state
        ) {



            items(it,
                key = {
                    it.uuId
                }) { item ->
                val imageUrl = item.fullHDURL ?: item.largeImageURL ?: item.imageURL ?: item.previewURL
                ImageItem(imageUrl, openImageDetail, context)
            }
        }


    }

}


@Composable
private fun ImageItem(imageUrl: String?, openImageDetail: (String) -> Unit, context: Context) {
    AsyncImage(
        modifier = Modifier
            .clickable {
                imageUrl?.let {
                    openImageDetail.invoke(it)
                    return@clickable
                }
            }
            .fillMaxWidth()
            .size(120.dp),
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.FillWidth
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {

    TopAppBar(
        title = { Text(text = "hasan balaban")},
        colors = TopAppBarDefaults.smallTopAppBarColors(Color.Green),
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.ime ,
        actions = {


            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
        }
    )

}