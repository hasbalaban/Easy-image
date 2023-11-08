package com.easyImage.mediapi.utils

fun String?.ignoreNull(defaultValue: String = ""): String = this ?: defaultValue