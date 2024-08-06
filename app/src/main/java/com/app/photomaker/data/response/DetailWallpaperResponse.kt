package com.app.photomaker.data.response


import com.app.photomaker.data.model.Status
import com.app.photomaker.data.model.Wallpaper
import com.google.gson.annotations.SerializedName

data class DetailWallpaperResponse(
    @SerializedName("data")
    val wallpaper: Wallpaper,
    @SerializedName("status")
    val status: Status
)