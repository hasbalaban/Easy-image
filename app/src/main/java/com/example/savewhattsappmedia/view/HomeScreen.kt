package com.example.savewhattsappmedia.view

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
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

    Column {
        ObserveCounter(photos.value, shouldGetNewPage, state, openImageDetail){
            viewModel.getPhotos()
        }
    }

}

@Composable
private fun ObserveCounter(
    photos: ImageResponse?,
    shouldGetNewPage: Boolean,
    state: LazyGridState,
    openImageDetail : (String) -> Unit,
    scrollToBottomOfPhotos : () -> Unit
) {
    photos?.hits?.let {
        val context = LocalContext.current


        SideEffect{
            if (shouldGetNewPage){
                scrollToBottomOfPhotos.invoke()
            }
        }

        Column() {

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
                    }
                ) { item ->
                    val imageUPreviewUrl = item.imageURL ?: item.largeImageURL ?: item.fullHDURL ?: item.previewURL

                    AsyncImage(
                        modifier = Modifier
                            .clickable {
                                val imageUrl = item.fullHDURL ?: item.largeImageURL ?: item.imageURL ?: item.previewURL

                                imageUrl?.let {
                                    openImageDetail.invoke(it)
                                    return@clickable
                                }
                            }
                            .fillMaxWidth()
                            .size(120.dp),
                        model = ImageRequest.Builder(context)
                            .data(imageUPreviewUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }


}
