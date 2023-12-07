package com.android.media.mediaPlayer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun MediaPlayer(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    mediaPlayer: ExoPlayer
) {
    DisposableEffect(AndroidView(modifier = modifier, factory = {
        PlayerView(context).apply {
            player = mediaPlayer.apply {
                setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                //addListener(listener)
               // prepare()
               // play()

                useController = true

            }
        }
    }
    )){
        onDispose {
            ExoPlayerManager.releasePlayer(exoPlayer = mediaPlayer)
        }
    }
}