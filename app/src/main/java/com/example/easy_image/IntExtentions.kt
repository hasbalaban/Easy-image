package com.example.easy_image

fun Int?.ignoreNull(defaultValue : Int = 0) = this ?: defaultValue