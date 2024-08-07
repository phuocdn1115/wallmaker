package com.app.imagetovideo.data.response


import com.app.imagetovideo.data.model.Status
import com.app.imagetovideo.data.model.Wallpaper
import com.google.gson.annotations.SerializedName

data class DetailWallpaperResponse(
    @SerializedName("data")
    val wallpaper: Wallpaper,
    @SerializedName("status")
    val status: Status
)