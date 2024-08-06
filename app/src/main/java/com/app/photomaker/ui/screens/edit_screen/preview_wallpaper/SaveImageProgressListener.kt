package com.app.photomaker.ui.screens.edit_screen.preview_wallpaper

import com.app.photomaker.data.realm_model.WallpaperDownloaded

interface SaveImageProgressListener {
    fun onSuccess(objectImageVideoFragment: WallpaperDownloaded)
    fun onProgress()
    fun onFailure(error : String)
}