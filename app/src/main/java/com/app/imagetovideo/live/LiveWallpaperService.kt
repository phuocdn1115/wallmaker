package com.app.imagetovideo.live

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.app.imagetovideo.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LiveWallpaperService : WallpaperService() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreateEngine(): Engine {
        return LiveWallpaperEngine(
            VideoEngine(
                applicationContext,
                preferencesManager
            )
        )
    }

    inner class LiveWallpaperEngine(private val engine: WallpaperEngine) : WallpaperService.Engine() {
        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            engine.onSurfaceChanged(holder, format, width, height)
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            engine.setup(this, surfaceHolder)
            if (engine is VideoEngine) {
                setTouchEventsEnabled(true)
            }
        }

        override fun onTouchEvent(event: MotionEvent) {
            engine.onTouchEvent(event)
            super.onTouchEvent(event)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            engine.onSurfaceCreated(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            engine.onVisibilityChanged(visible)
            super.onVisibilityChanged(visible)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            engine.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            super.onDestroy()
        }
    }
}