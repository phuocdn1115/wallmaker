package com.app.imagetovideo.data.model

import com.app.imagetovideo.model.Data
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded

class ImageMadeByUser(
    var id: String = "",
    var createTime: Long = 0,
    var name: String = "",
    var wallpaperType: String = "",
    var pathInStorage: String = "",
    var imageThumb: String? = ""
) : Data() {
    fun convertToWallpaperDownloaded(): WallpaperDownloaded {
        var wallpaperDownloaded = WallpaperDownloaded()
        wallpaperDownloaded.name = this.name
        wallpaperDownloaded.id = this.id
        wallpaperDownloaded.createTime = this.createTime
        wallpaperDownloaded.wallpaperType = this.wallpaperType
        wallpaperDownloaded.pathInStorage = this.pathInStorage
        wallpaperDownloaded.imageThumb = this.imageThumb
        return wallpaperDownloaded
    }
}