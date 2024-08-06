package com.app.photomaker.data.response

import com.app.photomaker.data.model.Status
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("status")
    val status: Status? = null
}
