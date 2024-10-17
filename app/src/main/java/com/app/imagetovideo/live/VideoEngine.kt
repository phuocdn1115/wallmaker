package com.app.imagetovideo.live

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.app.imagetovideo.PreferencesKey.URL_WALLPAPER_LIVE_IF_PREVIEW
import com.app.imagetovideo.PreferencesKey.URL_WALLPAPER_LIVE_IF_SET
import com.app.imagetovideo.PreferencesManager
import com.app.imagetovideo.WallpaperMakerApp

class VideoEngine(private val context: Context, private val preferencesManager: PreferencesManager): WallpaperEngine() {
    private var exoPlayer: ExoPlayer? = null
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

    @OptIn(UnstableApi::class)
    @Synchronized
    private fun playVideo() {
        val holder = surfaceHolder ?: return
        if (!isVisible) return
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                setVideoSurfaceHolder(holder)
                videoScalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                volume = 0f
                repeatMode = Player.REPEAT_MODE_ONE
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
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
//            val factory = if (temp.startsWith("content://")) DataSource.Factory {
//                ContentDataSource(WallpaperMakerApp.instance).apply {
//                    open(DataSpec(uri))
//                }
//            }
//            else DataSource.Factory { FileDataSource() }
//            val dataSource = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(uri))
            exoPlayer?.apply {
                setMediaItem(MediaItem.fromUri(uri))
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
            exoPlayer?.stop()
            lastNameUrl = null
        } catch (e: Exception) {

        }
    }
}