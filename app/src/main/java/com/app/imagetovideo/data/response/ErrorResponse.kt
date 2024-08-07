package com.app.imagetovideo.data.response

import com.app.imagetovideo.data.model.Status
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("status")
    val status: Status? = null
}
