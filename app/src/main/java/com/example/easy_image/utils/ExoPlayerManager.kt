package com.example.easy_image.utils

import android.content.Context
import android.util.Log
import androidx.media3.common.C
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
import java.io.File


object ExoPlayerManager {
    private val players: MutableList<ExoPlayer> = mutableListOf()

    fun initializePlayer(context: Context , videoUri: String): ExoPlayer {
        CacheManager.initialize(context)
        val player = players.firstOrNull { it.playbackState == Player.STATE_IDLE }?.also {
            it.setHandleAudioBecomingNoisy(true)
            setMediaItem(it, videoUri)
            it.prepare()
        }
            ?: createNewPlayer(context, videoUri = videoUri)
        players.remove(player)
        return player
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun createNewPlayer(context: Context, videoUri: String): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        player.setHandleAudioBecomingNoisy(true)
        player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        setMediaItem(player, videoUri)
        player.prepare()
        return player
    }
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setMediaItem(
        exoPlayer: ExoPlayer,
        videoUri: String,
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
    }

    fun releasePlayer(exoPlayer: ExoPlayer) {
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

object CacheManager {
    private lateinit var cache: SimpleCache

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initialize(context: Context) {
        if (!::cache.isInitialized) {
            clearApplicationData(context)
            val cacheDirectory = File(context.cacheDir, "ExoplayerCache")
            val maxCacheSize = 400 * 1024 * 1024 // 200 MB cache size
            val evictor = LeastRecentlyUsedCacheEvictor(maxCacheSize.toLong())
            val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
            cache = SimpleCache(cacheDirectory, evictor, databaseProvider)
        }
    }

    fun getCache(): SimpleCache {
        return cache
    }
}

fun clearApplicationData(context: Context) {
    val cache: File = context.cacheDir
    val appDir = cache.parent?.let { File(it) }
    if (appDir?.exists() == true) {
        val children = appDir.list()
        children?.let {
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i(
                        "TAG",
                        "**************** File /data/data/APP_PACKAGE/$s DELETED *******************"
                    )
                }
            }
        }
    }
}

fun deleteDir(dir: File): Boolean {
    if (dir.exists() && dir.isDirectory) {
        val children = dir.list()
        children?.forEach { child ->
            val childFile = File(dir, child)
            val success = deleteDir(childFile)
            if (!success) {
                return false
            }
        }
    }
    return dir.delete()
}





