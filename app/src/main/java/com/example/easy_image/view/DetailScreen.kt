package com.example.easy_image.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.easy_image.SaveImageToCacheAndShare
import com.example.easy_image.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(imageUrl: String, popBackStack: () -> Unit) {

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


    //systemUiController.setStatusBarColor(Color.Magenta)
    // Hide the status bar
    // systemUiController.isStatusBarVisible = false
    //systemUiController.isNavigationBarVisible = false
    //systemUiController.isNavigationBarContrastEnforced = true

    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false



    val loader = ImageLoader(LocalContext.current)
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(imageUrl)
        .allowHardware(false)
        .crossfade(true)
        .build()

    val coroutineScope = rememberCoroutineScope()
    SideEffect {
        coroutineScope.launch {
            val result = (loader.execute(imageRequest) as SuccessResult).drawable
            bitmap = (result as BitmapDrawable).bitmap
        }
    }
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        scale *= event.calculateZoom()
                        val offset = event.calculatePan()
                        offsetX += offset.x
                        offsetY += offset.y

                    } while (event.changes.any { it.pressed })
                scale = 1f
                offsetX = 1f
                offsetY = 1f
            }
        }

    ) {
        val (shareButton, backButton) = createRefs()

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            model = imageRequest,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )

        Image(
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .clickable {
                    popBackStack.invoke()
                }.padding(12.dp).size(24.dp),
            painter = painterResource(id = R.drawable.back_button),
            contentDescription = "save Button"
        )
        Image(
            modifier = Modifier
                .constrainAs(shareButton) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .clickable {
                    bitmap?.asImageBitmap()?.let {
                        SaveImageToCacheAndShare().saveImageToCache(it, context = context)
                    }
                }.padding(12.dp).size(24.dp),
            painter = painterResource(id = R.drawable.share_icon),
            contentDescription = "save Button"
        )

    }

}




@Preview
@Composable
private fun PreviewDetailScreen(){
    SearchScreen()
}