package com.app.photomaker.data.realm_model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WallpaperSuggestionDownloaded : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var createTime: Long = 0
    var name: String = ""
    var pathInStorage: String = ""
}