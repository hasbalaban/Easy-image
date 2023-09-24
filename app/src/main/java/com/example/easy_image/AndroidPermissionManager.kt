package com.example.easy_image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat


@Composable
fun permissionRequest () = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            // Handle the result
        }
    )

 fun Context.isPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
            ||
     ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}