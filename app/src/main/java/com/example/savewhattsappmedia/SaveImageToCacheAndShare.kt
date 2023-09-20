package com.example.savewhattsappmedia

import android.R.attr
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar


class SaveImageToCacheAndShare {
    fun saveImageToCache( image: ImageBitmap, context: Context) {
        try {
            val cacheDirectory: File = File(context.cacheDir, "images")
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs()
            }
            val imageFile = File(cacheDirectory, "image.jpg")
            val outputStream = FileOutputStream(imageFile)
            val bitmap = image.asAndroidBitmap()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            outputStream.flush()
            outputStream.close()

            shareImage(context)
            saveToInternalStorage(context = context, bitmap = bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareImage(context: Context){

        val cacheDirectory: File = File(context.cacheDir, "images")
        val imageFile = File(cacheDirectory, "image.jpg")

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
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

    private fun saveToInternalStorage(context: Context, bitmap: Bitmap){
        val fileName =  System.currentTimeMillis().toString() + ".jpeg"
        val albumName = Calendar.DAY_OF_YEAR.toString()
        val contentValues = ContentValues()

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH,
            Environment.DIRECTORY_DCIM + File.separator + albumName
        )

        val imageUri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if (imageUri != null){
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(imageUri)
                if (outputStream != null){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
