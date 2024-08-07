package com.app.imagetovideo.data.model

import com.app.imagetovideo.model.Data
import java.io.Serializable

class Template (
    val id: String? = "",
    val name: String? = "",
    var script: List<ObjectSegmentData>?= ArrayList(),
    val type: String ?= "",
    val url: String? = "",
    val thumbUrlImg: String? = "",
    val thumbUrlVideo: String? = "",
    val colorCode: String? = ""
)  : Serializable, Data(){

    fun originUrlString(): String {
        return "$url"
    }

    fun thumbUrlVideoString(): String{
        return "$thumbUrlVideo"
    }

    fun thumbUrlImageString(): String{
        return "$thumbUrlImg"
    }

    fun clone() : Template {
        var newScript : ArrayList<ObjectSegmentData> = ArrayList<ObjectSegmentData>()
        this.script?.let { newScript.addAll(it.toList()) }
        return Template(this.id, this.name, newScript, this.type, this.url, this.thumbUrlImg, this.thumbUrlVideoString(), this.colorCode)
    }
}