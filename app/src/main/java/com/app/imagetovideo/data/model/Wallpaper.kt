package com.app.imagetovideo.data.model

import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.model.Data
import com.app.imagetovideo.utils.FileUtils
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
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

}