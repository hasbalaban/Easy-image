package com.example.easy_image.view

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.easy_image.BuildConfig
import com.example.easy_image.R
import com.example.easy_image.utils.ExoPlayerManager
import com.example.easy_image.utils.SaveImageToCacheAndShare
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat


@Composable
fun DetailScreen(dataUrl: String, isImage: Boolean = true, popBackStack: () -> Unit) {


    if (isImage) {

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

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(dataUrl)
                .listener { _, result ->
                    if (bitmap == null){
                        bitmap = (result.drawable  as BitmapDrawable).bitmap
                    }                }
                .size(Size.ORIGINAL)
                .build()
        )

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


            val (shareButton, backButton, saveButton) = createRefs()

            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                painter = painter,
                contentScale = ContentScale.FillBounds,
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
                    }
                    .padding(12.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.back_button),
                contentDescription = "save Button"
            )
            Image(
                modifier = Modifier
                    .constrainAs(saveButton) {
                        top.linkTo(parent.top)
                        end.linkTo(shareButton.start)
                    }
                    .clickable {


                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            println("sdsd")
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)

                                startActivity(
                                    context,
                                    Intent(
                                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                        uri
                                    ),
                                    null
                                )

                                return@clickable
                            }

                            //val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            val contentValues = ContentValues()
                            contentValues.apply {
                                val simpleDateFormat = SimpleDateFormat("MM-dd-HH-mm-ss")
                                val imageName = simpleDateFormat.format(System.currentTimeMillis())


                                // val imageName = getFileName(System.currentTimeMillis())
                                put(MediaStore.Images.Media.DISPLAY_NAME, "image-${imageName}.jpg")
                                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            }

                            val uri: Uri? = context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                            )
                            val outputStream: OutputStream? = uri?.let {
                                context.contentResolver.openOutputStream(it)
                            }

                            if (outputStream != null && bitmap != null) {
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }

                            outputStream?.close()
                        }
                    }
                    .padding(12.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.share_icon),
                contentDescription = "save Button"
            )
            Image(
                modifier = Modifier
                    .constrainAs(shareButton) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        bitmap
                            ?.asImageBitmap()
                            ?.let {
                                SaveImageToCacheAndShare().saveImageToCache(it, context = context)
                            }
                    }
                    .padding(12.dp)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.share_icon),
                contentDescription = "Share Button"
            )


        }
    } else {
        VideoDetailScreen(dataUrl)
    }

}


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun VideoDetailScreen(videoUrl: String) {
    val context = LocalContext.current

    val exoPlayer by remember { mutableStateOf(ExoPlayerManager.createNewPlayer(context, videoUrl)) }
    var fraction by remember { mutableStateOf(1.0f) }


    LaunchedEffect(Unit){
        while (true){
            withContext(Dispatchers.IO){
                delay(400)
                withContext(Dispatchers.Main){
                    val duration = (exoPlayer.duration / 1000).toFloat()
                    val currentPosition = (exoPlayer.currentPosition / 1000).toFloat()
                    val percent = (currentPosition / duration)

                    if (percent > 0f) {
                        fraction = percent
                    }
                }
            }
        }
    }

    Box(contentAlignment = Alignment.BottomStart) {
        DisposableEffect(AndroidView(modifier = Modifier
            .fillMaxSize(), factory = {
            PlayerView(context).apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                player = exoPlayer.apply {
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    useController = false
                }
            }
        }
        )) {
            onDispose {
                exoPlayer.release()
            }
        }


        Box(
            Modifier.height(3.dp).fillMaxWidth().padding(bottom = 4.dp),
            contentAlignment = Alignment.BottomStart
        ) {

            VideoTimeLineBar(fraction = fraction) {
                val newValue = exoPlayer.duration * it
                exoPlayer.seekTo(newValue.toLong())
            }
        }

        //Duration(fraction = fraction)
    }


    LaunchedEffect(Unit) {

        val mediaItem = MediaItem.fromUri(videoUrl)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
        exoPlayer.play()
    }

}

@Composable
private fun Duration(fraction : Float){

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .background(Color.Black)
        .padding(vertical = 2.dp, horizontal = 4.dp)) {

        Spacer(modifier = Modifier
            .background(Color.White)
            .fillMaxHeight()
            .fillMaxWidth(fraction))

    }
}

@Composable
fun VideoTimeLineBar(
    fraction: Float,
    onValueChange: (Float) -> Unit
) {

    Slider(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .padding(end = 6.dp),
        value = fraction,
        onValueChange = onValueChange,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.DarkGray,
        )
    )
}


@Preview
@Composable
private fun PreviewDetailScreen() {
    Box(
        Modifier.height(2.dp).fillMaxWidth().padding(bottom = 4.dp),
        contentAlignment = Alignment.BottomStart
    ) {

        VideoTimeLineBar(0.5f){

        }
    }

}