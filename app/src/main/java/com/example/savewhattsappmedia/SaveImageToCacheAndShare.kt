package com.example.savewhattsappmedia

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class SaveImageToCacheAndShare {
    fun saveImageToCache( image: ImageBitmap, context: Context) {
        try {
            val cacheDirectory: File = File(context.cacheDir, "images")
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs()
            }
            val imageFile = File(cacheDirectory, "image.jpg")
            val outputStream = FileOutputStream(imageFile)
            image.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            shareImage(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareImage(context: Context){
        val cacheDirectory: File = File(context.cacheDir, "images")
        val imageFile = File(cacheDirectory, "image.jpg")

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                imageFile
            )
        )
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
}