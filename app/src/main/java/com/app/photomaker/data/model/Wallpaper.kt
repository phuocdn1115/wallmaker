package com.app.photomaker.data.model

import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.enums.WallpaperType
import com.app.photomaker.model.Data
import com.app.photomaker.utils.FileUtils
import com.app.photomaker.data.realm_model.WallpaperDownloaded
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Wallpaper(
    @SerializedName("colorCode")
    val colorCode: String? = "",
    @SerializedName("colorId")
    val colorId: Int? = 0,
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("screenRatio")
    val screenRatio: String? = "",
    @SerializedName("script")
    val script: ArrayList<ObjectSegmentData>?= null,
    @SerializedName("type")
    val type: String ?= "",
    @SerializedName("url")
    var url: String? = "",
    @SerializedName("thumbUrlImg")
    var thumbUrlImg: String? = "",
    @SerializedName("thumbUrlVideo")
    var thumbUrlVideo: String? = "",
    var urlShare: String? = "",
    var pathCacheTemplate : String ?= null
)  : Serializable, Data() {
    fun minThumbURLString(): String {
        return if (!type.isNullOrBlank() ) {
            val fileName = url?.let { FileUtils.getFileName(it) }
            val lastPath = "thumbs/" + (fileName?.replace(".mp4", ".jpg") ?: "")
            val path = fileName?.let { url?.replace(it, lastPath) }
            "${ ApplicationContext.getNetworkContext().videoURL}$path"
        } else {
            "${ApplicationContext.getNetworkContext().imageURL}minthumbnails/$url"
        }
    }

    fun originUrlString(): String {
          return "${ApplicationContext.getNetworkContext().videoURL}$url"
    }

    fun convertToTemplateObject() : Template {
        return Template(
            id = this.id,
            name = this.name,
            script = this.script,
            type = this.type,
            url = this.url,
            thumbUrlImg = this.thumbUrlImg,
            thumbUrlVideo = this.thumbUrlVideo,
            colorCode = this.colorCode
        )
    }

    fun convertToWallpaperDownloaded(): WallpaperDownloaded {
        var wallpaperDownloaded = WallpaperDownloaded()
        wallpaperDownloaded.name = this.name.toString()
        wallpaperDownloaded.id = this.id.toString()
        wallpaperDownloaded.wallpaperType = WallpaperType.VIDEO_SUGGESTION_TYPE.value
        wallpaperDownloaded.pathInStorage = this.url.toString()
        wallpaperDownloaded.imageThumb = this.minThumbURLString()
        return wallpaperDownloaded
    }

    fun convertToWallpaperDownloaded(pathInStorage: String): WallpaperDownloaded {
        var wallpaperDownloaded = WallpaperDownloaded()
        wallpaperDownloaded.name = this.name.toString()
        wallpaperDownloaded.id = this.id.toString()
        wallpaperDownloaded.wallpaperType = WallpaperType.VIDEO_SUGGESTION_TYPE.value
        wallpaperDownloaded.pathInStorage = pathInStorage
        wallpaperDownloaded.imageThumb = this.minThumbURLString()
        return wallpaperDownloaded
    }
}