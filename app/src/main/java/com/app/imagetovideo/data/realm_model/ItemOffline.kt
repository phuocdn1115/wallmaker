package com.app.imagetovideo.data.realm_model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ItemOffline() : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var data: String? = null
}