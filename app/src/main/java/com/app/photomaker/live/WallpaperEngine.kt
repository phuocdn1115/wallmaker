package com.app.photomaker.live

import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import java.lang.ref.WeakReference

abstract class WallpaperEngine {
    private var _engine: WeakReference<WallpaperService.Engine>? = null

    private var mSurfaceHolder: WeakReference<SurfaceHolder>? = null

    protected val isVisible get() = _engine?.get()?.isVisible ?: false
    protected var width = 0
    protected var height = 0

    val isPreview : Boolean
        get() = _engine?.get()?.isPreview ?: false


    fun setup(engine: WallpaperService.Engine, surfaceHolder: SurfaceHolder) {
        this._engine = WeakReference(engine)
        this.mSurfaceHolder = WeakReference(surfaceHolder)
    }

    abstract fun onSurfaceCreated(holder: SurfaceHolder)

    fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    open fun onVisibilityChanged(visible: Boolean)  {}

    open fun onTouchEvent(event: MotionEvent) {}

    open fun onSurfaceDestroyed(holder: SurfaceHolder) {
        _engine = null
        mSurfaceHolder = null
    }


    val surfaceHolder: SurfaceHolder?
        get() = mSurfaceHolder?.get()
    val engine: WallpaperService.Engine? get() = _engine?.get()
}