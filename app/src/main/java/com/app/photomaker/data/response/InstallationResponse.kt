package com.app.photomaker.data.response

import com.google.gson.annotations.SerializedName
import com.app.photomaker.data.model.Status

data class InstallationResponse(
    @SerializedName("data")
    val `data`: String,
    @SerializedName("status")
    val status: Status
)