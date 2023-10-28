package com.example.easy_image.utils.permission

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi


var storage_permissions = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE
)

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
var storage_permissions_33 = arrayOf(
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_AUDIO,
    Manifest.permission.READ_MEDIA_VIDEO
)

fun getPermissions(): Array<String> {
    val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        storage_permissions_33
    } else {
        storage_permissions
    }
    return permissions
}
