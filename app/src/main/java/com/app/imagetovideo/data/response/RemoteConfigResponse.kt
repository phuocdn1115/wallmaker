package com.app.imagetovideo.data.response

import com.google.gson.annotations.SerializedName

data class RemoteConfigResponse(

    //version
    @SerializedName("appVersion")
    val appVersion: String,
    @SerializedName("appVersionCode")
    val appVersionCode: Int,

    @SerializedName("apiUrls")
    val apiUrls: String?,
    //url
    @SerializedName("apiUrlAmerica")
    val apiUrlAmerica: String,
    @SerializedName("apiUrlAsia")
    val apiUrlAsia: String,
    @SerializedName("apiUrlEu")
    val apiUrlEu: String,
    @SerializedName("apiUrlGlobal")
    val apiUrlGlobal: String,
    @SerializedName("apiUrlVn")
    val apiUrlVn: String,

    //
    @SerializedName("vn")
    val vn: String,
    @SerializedName("america")
    val america: String,
    @SerializedName("asia")
    val asia: String,
    @SerializedName("eu")
    val eu: String,

    //
    @SerializedName("connectTimeout")
    val connectTimeout: Int,
    @SerializedName("readTimeout")
    val readTimeout: Int,
    @SerializedName("retry")
    val retry: Int,

    //ads unit
    @SerializedName("adsNativeInHomeId")
    val adsNativeInHomeId: String,
    @SerializedName("adsNativeInFrameId")
    val adsNativeInFrameId: String,
    @SerializedName("adsNativeInSetSuccessId")
    val adsNativeInSetSuccessId: String,
    @SerializedName("adsRewardInPreviewId")
    val adsRewardInPreviewId: String,
    @SerializedName("adsOpenAdsId")
    val adsOpenAdsId: String,
    @SerializedName("adsBannerId")
    val adsBannerId: String,

    //video url
    @SerializedName("videoUrlGlobal")
    val videoUrlGlobal: String,
    @SerializedName("videoUrlAmerica")
    val videoUrlAmerica: String,
    @SerializedName("videoUrlEu")
    val videoUrlEu: String,
    @SerializedName("videoUrlAsia")
    val videoUrlAsia: String,
    @SerializedName("videoUrlVn")
    val videoUrlVn: String

) {

}