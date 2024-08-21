package com.app.imagetovideo.aplication

import android.util.Log
import com.app.imagetovideo.data.response.RemoteConfigResponse

private val networkContext = NetworkContext()
private val deviceContext = DeviceContext()
private val adsContext = AdsContext()
object ApplicationContext {
    fun getNetworkContext(): NetworkContext = networkContext
    fun getDeviceContext() : DeviceContext = deviceContext
    fun getAdsContext() : AdsContext = adsContext
    val sessionContext = SessionContext()
}