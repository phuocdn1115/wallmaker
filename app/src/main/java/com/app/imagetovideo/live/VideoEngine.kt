package com.app.imagetovideo.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.SurfaceHolder
import com.app.imagetovideo.PreferencesKey.URL_WALLPAPER_LIVE_IF_PREVIEW
import com.app.imagetovideo.PreferencesKey.URL_WALLPAPER_LIVE_IF_SET
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.WallpaperMakerApp
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.ContentDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource

class VideoEngine(private val context: Context, private val preferencesManager: PreferencesManager): WallpaperEngine() {
    private var exoPlayer: SimpleExoPlayer? = null
    private var lastNameUrl: String? = null

    override fun onSurfaceCreated(holder: SurfaceHolder) {

    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            playVideo()
        } else {
            stopVideo()
        }
    }

    override fun onSurfaceDestroyed(holder: SurfaceHolder) {
        release()
    }

    @Synchronized
    private fun playVideo() {
        val holder = surfaceHolder ?: return
        if (!isVisible) return
        if (exoPlayer == null) {
            exoPlayer = SimpleExoPlayer.Builder(context).build().apply {
                setVideoSurfaceHolder(holder)
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                volume = 0f
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(object : Player.EventListener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        lastNameUrl = null
                        if (isPreview) {
                            context.stopService(Intent(WallpaperMakerApp.instance, LiveWallpaperService::class.java))
                        }
                    }
                })
            }
        }
        try {
            val uri = getUri()
            val temp = uri.toString()
            if (lastNameUrl == temp) {
                exoPlayer?.playWhenReady = true
                return
            }
            val factory = if (temp.startsWith("content://")) DataSource.Factory {
                ContentDataSource(WallpaperMakerApp.instance).apply {
                    open(DataSpec(uri))
                }
            }
            else DataSource.Factory { FileDataSource() }
            val dataSource = ProgressiveMediaSource.Factory(factory).createMediaSource(uri)
            exoPlayer?.apply {
                prepare(dataSource)
                playWhenReady = true
                lastNameUrl = temp
            }
        } catch (e: Exception) {
        }
    }

    private fun getUri(): Uri {
        val path = if (isPreview) preferencesManager.getString(URL_WALLPAPER_LIVE_IF_PREVIEW) else preferencesManager.getString(URL_WALLPAPER_LIVE_IF_SET)
        return Uri.parse(path)
    }

    @Synchronized
    private fun release() {
        try {
            exoPlayer?.release()
            lastNameUrl = null
            exoPlayer = null
        } catch (e: Exception) {

        }
    }

    @Synchronized
    private fun stopVideo() {
        try {
            exoPlayer?.stop(true)
            lastNameUrl = null
        } catch (e: Exception) {

        }
    }
}