package com.example.easy_image.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.easy_image.model.VideoItemDTO
import java.io.File


object ExoPlayerManager {
    private val players: MutableList<ExoPlayer> = mutableListOf()

    fun initializePlayer(context: Context): ExoPlayer {
        CacheManager.initialize(context)
        val player = players.firstOrNull { it.playbackState == Player.STATE_IDLE }
            ?: createNewPlayer(context)
        players.remove(player)
        return player
    }

    private fun createNewPlayer(context: Context): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        player.setHandleAudioBecomingNoisy(true)
        return player
    }
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setMediaItem(
        exoPlayer: ExoPlayer,
        videoUri: String,
        playbackPosition: Int = 0,
    ) {
        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(CacheManager.getCache())
                .setUpstreamDataSourceFactory(
                    DefaultHttpDataSource.Factory()
                        .setUserAgent("ExoPlayer"))

        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUri))

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun releasePlayer(exoPlayer: ExoPlayer, listItems: VideoItemDTO) {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        players.add(exoPlayer)
    }

    fun releaseAllPlayers() {
        for (player in players) {
            player.release()
        }
        players.clear()
    }
}




