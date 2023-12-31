package com.android.media.mediaPlayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.exoplayer.ExoPlayer


@Composable
fun rememberMediaPlayer(
): ExoPlayer {
    val context = LocalContext.current
    val player = remember {
        mutableStateOf(
            ExoPlayerManager.initializePlayer(
                context
            )
        )
    }

    return player.value
}
