package com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper

import com.app.imagetovideo.data.realm_model.WallpaperDownloaded

interface SaveImageProgressListener {
    fun onSuccess(objectImageVideoFragment: WallpaperDownloaded)
    fun onProgress()
    fun onFailure(error : String)
}