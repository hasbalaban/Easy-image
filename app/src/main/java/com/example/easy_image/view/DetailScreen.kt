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
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.easy_image.BuildConfig
import com.example.easy_image.R
import com.example.easy_image.utils.SaveImageToCacheAndShare
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat


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
        val (shareButton, backButton, saveButton) = createRefs()

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
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri),
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
                    bitmap?.asImageBitmap()?.let {
                        SaveImageToCacheAndShare().saveImageToCache(it, context = context)
                    }
                }.padding(12.dp).size(24.dp),
            painter = painterResource(id = R.drawable.share_icon),
            contentDescription = "Share Button"
        )

    }

}




@Preview
@Composable
private fun PreviewDetailScreen(){
    SearchScreen()
}