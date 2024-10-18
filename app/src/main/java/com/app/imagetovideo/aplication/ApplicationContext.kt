package com.app.imagetovideo.aplication

import android.util.Log

private val networkContext = NetworkContext()
private val adsContext = AdsContext()
object ApplicationContext {
    fun getNetworkContext(): NetworkContext = networkContext
    fun getAdsContext() : AdsContext = adsContext
    val sessionContext = SessionContext()
}