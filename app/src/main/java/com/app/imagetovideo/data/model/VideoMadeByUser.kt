package com.app.imagetovideo.data.model

import com.app.imagetovideo.model.Data
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded

class VideoMadeByUser(
    var id: String = "",
    var createTime: Long = 0,
    var name: String = "",
    var wallpaperType: String = "",
    var pathInStorage: String = "",
    var imageThumb: String? = "",
    var isTemplate: Boolean = false,
    var idTemplate: String = ""
) : Data() {
    fun convertToWallpaperDownloaded(): WallpaperDownloaded {
        var wallpaperDownloaded = WallpaperDownloaded()
        wallpaperDownloaded.name = this.name
        wallpaperDownloaded.id = this.id
        wallpaperDownloaded.createTime = this.createTime
        wallpaperDownloaded.wallpaperType = this.wallpaperType
        wallpaperDownloaded.pathInStorage = this.pathInStorage
        wallpaperDownloaded.imageThumb = this.imageThumb
        wallpaperDownloaded.isTemplate = this.isTemplate
        wallpaperDownloaded.idTemplate = this.idTemplate
        return wallpaperDownloaded
    }
}