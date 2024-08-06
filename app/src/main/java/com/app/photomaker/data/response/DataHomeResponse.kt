package com.app.photomaker.data.response


import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.data.model.Status
import com.google.gson.annotations.SerializedName

data class DataHomeResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: Status?
){
    data class Data(
        @SerializedName("currentPageNumber")
        val currentPageNumber: Int? = 0,
        @SerializedName("data")
        val `data`: List<Wallpaper>,
        @SerializedName("nextPageNumber")
        val nextPageNumber: Int? = 0
    )
}