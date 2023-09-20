package com.example.savewhattsappmedia

fun Int?.ignoreNull(defaultValue : Int = 0) = this ?: defaultValue