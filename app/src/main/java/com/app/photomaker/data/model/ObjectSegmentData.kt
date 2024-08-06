package com.app.photomaker.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ObjectSegmentData(
    @SerializedName("duration")
    var duration: Int = 0,
    @SerializedName("index")
    var index: Int = 0,
    @SerializedName("segmentType")
    var segmentType: String? = null
): Serializable