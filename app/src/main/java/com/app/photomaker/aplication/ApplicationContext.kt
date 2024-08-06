package com.app.photomaker.aplication

import android.util.Log
import com.app.photomaker.data.response.RemoteConfigResponse

private val networkContext = NetworkContext()
private val deviceContext = DeviceContext()
private val adsContext = AdsContext()
object ApplicationContext {
    fun getNetworkContext(): NetworkContext = networkContext
    fun getDeviceContext() : DeviceContext = deviceContext
    fun getAdsContext() : AdsContext = adsContext
    val sessionContext = SessionContext()

    /**
     * Update Video URL
     */
    fun updateVideoUrl(configResult: RemoteConfigResponse) {
        if (!configResult.videoUrlGlobal.isNullOrEmpty())
            networkContext.videoUrlGlobal = configResult.videoUrlGlobal
        if (!configResult.videoUrlAmerica.isNullOrEmpty())
            networkContext.videoUrlAmerica = configResult.videoUrlAmerica
        if (!configResult.videoUrlEu.isNullOrEmpty())
            networkContext.videoUrlEu = configResult.videoUrlEu
        if (!configResult.videoUrlAsia.isNullOrEmpty())
            networkContext.videoUrlAsia = configResult.videoUrlAsia
        if (!configResult.videoUrlVn.isNullOrEmpty())
            networkContext.videoUrlVn = configResult.videoUrlVn
    }

    fun updateAds(configResult: RemoteConfigResponse) {
        if (configResult.adsNativeInHomeId != null) adsContext.adsNativeInHomeId = configResult.adsNativeInHomeId
        if (configResult.adsNativeInFrameId != null) adsContext.adsNativeInFrameId = configResult.adsNativeInFrameId
        if (configResult.adsNativeInSetSuccessId != null) adsContext.adsNativeInSetSuccessId = configResult.adsNativeInSetSuccessId
        if (configResult.adsRewardInPreviewId != null) adsContext.adsRewardInPreviewId = configResult.adsRewardInPreviewId
        if (configResult.adsOpenAdsId != null) adsContext.adsOpenAdsId = configResult.adsOpenAdsId
        if (configResult.adsBannerId != null) adsContext.adsBannerId = configResult.adsBannerId
    }

    fun updateNetworkContext(configResult: RemoteConfigResponse) {
        Log.i("RemoteConfig","${configResult.apiUrls}")
        networkContext.initOrUpdateApiUrl(configResult.apiUrls ?: "")
    }

}