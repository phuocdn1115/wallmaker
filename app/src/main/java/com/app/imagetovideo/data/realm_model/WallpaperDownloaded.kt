package com.app.imagetovideo.data.realm_model

import com.app.imagetovideo.data.model.ImageMadeByUser
import com.app.imagetovideo.data.model.VideoMadeByUser
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class WallpaperDownloaded : RealmObject(), Serializable {
    @PrimaryKey
    var id: String = ""
    var createTime: Long = 0
    var name: String = ""
    var wallpaperType = ""
    var pathInStorage: String = ""
    var imageThumb: String ?= ""
    var idTemplate: String = ""
    var isTemplate: Boolean = false

    fun convertToVideoUser(): VideoMadeByUser {
        return VideoMadeByUser(
            id = this.id,
            pathInStorage = this.pathInStorage,
            name = this.name,
            wallpaperType = this.wallpaperType,
            imageThumb = this.imageThumb,
            createTime = this.createTime,
            isTemplate = this.isTemplate,
            idTemplate = this.idTemplate
        )
    }

    fun convertToImageUser(): ImageMadeByUser {
        return ImageMadeByUser(
            id = this.id,
            pathInStorage = this.pathInStorage,
            name = this.name,
            wallpaperType = this.wallpaperType,
            imageThumb = this.imageThumb,
            createTime = this.createTime
        )
    }
}