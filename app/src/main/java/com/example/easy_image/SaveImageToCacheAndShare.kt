package com.example.easy_image

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
import java.util.ArrayList
import java.util.Calendar


class SaveImageToCacheAndShare {
    fun saveImageToCache( image1: ImageBitmap, image2: ImageBitmap? = null, context: Context) {
        try {
            val cacheDirectory: File = File(context.cacheDir, "images")
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs()
            }
            val imageFile = File(cacheDirectory, "image.jpg")
            val imageFile1 = File(cacheDirectory, "image1.jpg")
            val outputStream = FileOutputStream(imageFile)
            val outputStream1 = FileOutputStream(imageFile1)
            val bitmap = image1.asAndroidBitmap()
            val bitmap1 = image2?.asAndroidBitmap()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            bitmap1?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream1)

            outputStream.flush()
            outputStream1.flush()
            outputStream.close()
            outputStream1.close()

            shareImage(context)
            saveToInternalStorage(context = context, bitmap = bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareImage(context: Context){

        val cacheDirectory: File = File(context.cacheDir, "images")
        val imageFile = File(cacheDirectory, "image.jpg")
        val imageFile1 = File(cacheDirectory, "image1.jpg")

        val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        shareIntent.type = "image/jpeg"
        val uri1 =  FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            imageFile
        )
        val uri2 =  FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            imageFile1
        )
        val a = ArrayList<Uri>()
        a.add(uri1)
        a.add(uri2)

        shareIntent.putParcelableArrayListExtra(
            Intent.EXTRA_STREAM,
            a
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
