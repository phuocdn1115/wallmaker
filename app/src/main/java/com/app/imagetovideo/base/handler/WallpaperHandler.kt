package com.app.imagetovideo.base.handler

import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.enums.WallpaperTypeSetting
import com.app.imagetovideo.utils.extension.crop
import java.io.IOException

object WallpaperHandler {
    private val UNSUPPORT_INCREMENTAL = listOf("V10.2.2.0.MALMIXM")

    fun setWallpaper(bitmap: Bitmap, type: WallpaperTypeSetting = WallpaperTypeSetting.HOME, allowCrop: Boolean = true, callBack: (isSuccess: Boolean) -> Unit,) {
        val manager = WallpaperManager.getInstance(WallpaperMakerApp.instance)
        try {
            val transformedBitmap = if (allowCrop) bitmap.crop() else bitmap
            when (type) {
                WallpaperTypeSetting.HOME -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) manager.setBitmap(transformedBitmap, null, false, WallpaperManager.FLAG_SYSTEM)
                    else manager.setBitmap(transformedBitmap)
                }
                WallpaperTypeSetting.LOCK -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) manager.setBitmap(transformedBitmap, null, false, WallpaperManager.FLAG_LOCK)
                    else manager.setBitmap(transformedBitmap)
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        manager.setBitmap(transformedBitmap, null, false, WallpaperManager.FLAG_SYSTEM)
                        manager.setBitmap(transformedBitmap, null, false, WallpaperManager.FLAG_LOCK)
                    }
                    else manager.setBitmap(transformedBitmap)
                }
            }
            callBack.invoke(true)
        } catch (e: IOException) {
            callBack.invoke(false)
        } catch (e: RuntimeException) {
            e.message?.let { Log.e("WallpaperHandler", it) }
        }
    }

    fun isSupportLiveWallpaper(): Boolean {
        return WallpaperMakerApp.instance.packageManager.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER)
    }
}