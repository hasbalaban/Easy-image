package com.example.easy_image.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.easy_image.SavedImages
import com.example.easy_image.model.FavoriteDTO

@Composable
fun FavoriteScreen(openImageDetail: (String) -> Unit) {
    val images by remember { mutableStateOf(SavedImages.savedImages) }

    Column() {
         LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                userScrollEnabled = true
            ) {

             items(images){
                 ImageItem(it){
                     openImageDetail(it)
                 }
             }

         }
    }
}

@Composable
private fun ImageItem(
    item: FavoriteDTO,
    openImageDetail: (String) -> Unit
) {

    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()) {
        val painter1 = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .size(Size.ORIGINAL)
                .build()
        )

        val aa by remember { derivedStateOf {painter1} }

        Image(
            painter = aa,
            contentDescription = null,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                        item.imageUrl.let { openImageDetail(it) }
                    })
                }
                .fillMaxWidth()
                .size(200.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}