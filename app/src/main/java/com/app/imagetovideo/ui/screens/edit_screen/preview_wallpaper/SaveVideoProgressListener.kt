package com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper

import com.app.imagetovideo.data.realm_model.WallpaperDownloaded

interface SaveVideoProgressListener {
    fun onSuccess(objectImageVideoFragment: WallpaperDownloaded)
    fun onProgress(progress : Int, total : Int)
    fun onFailure(nameFile : String)
}