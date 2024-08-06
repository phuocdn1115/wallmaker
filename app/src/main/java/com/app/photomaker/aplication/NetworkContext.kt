package com.app.photomaker.aplication

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.app.photomaker.BuildConfig
import com.app.photomaker.PreferencesKey.COUNTRY_CODE
import com.app.photomaker.config.RegionConfig.Companion.AMERICA_CONFIG
import com.app.photomaker.config.RegionConfig.Companion.ASIA_CONFIG
import com.app.photomaker.config.RegionConfig.Companion.EU_CONFIG
import com.app.photomaker.config.RegionConfig.Companion.VN_CONFIG
import com.app.photomaker.enums.NetworkType
import com.blankj.utilcode.util.Utils
import java.util.*

class NetworkContext {

    var videoUrlVn: String = BuildConfig.GLOBAL_STORAGE_VIDEO_URL
    var videoUrlAsia: String = BuildConfig.GLOBAL_STORAGE_VIDEO_URL
    var videoUrlEu: String = BuildConfig.GLOBAL_STORAGE_VIDEO_URL
    var videoUrlAmerica: String = BuildConfig.GLOBAL_STORAGE_VIDEO_URL
    var videoUrlGlobal: String = BuildConfig.GLOBAL_STORAGE_VIDEO_URL

    val TAG = this.javaClass.simpleName
    val preferenceManager : SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(Utils.getApp().applicationContext)}
    var isNetworkConnected: Boolean = true
    var networkType = NetworkType.MEDIUM
    var countryKey = Locale.getDefault().country
    var videoURL = BuildConfig.GLOBAL_STORAGE_VIDEO_URL?:""
    var imageURL = ""
    var regionConfigMap = hashMapOf<String, String>()

    var apiList = mutableListOf<String>()
    var bestUrl : String =  BuildConfig.GLOBAL_API_URL
    var bestImageUrl : String =  BuildConfig.GLOBAL_API_URL.replace("api/","")
    var chooseBestUrl : Boolean = false

    init {
        countryKey = preferenceManager.getString(COUNTRY_CODE, Locale.getDefault().country)
        initOrUpdateApiUrl(BuildConfig.API_URL)
        updateVideoUrl()
    }

    fun assignBestUrl(bestUrl : String = BuildConfig.GLOBAL_API_URL) {
        this.bestUrl = bestUrl
        this.bestImageUrl = this.bestUrl.replace("api/","")
        this.chooseBestUrl = true
    }

    fun initOrUpdateApiUrl(urls: String) {
        if (urls.isNullOrEmpty()) return
        val apiArray = urls.split(",")
        apiList.clear()
        apiList.addAll(apiArray)
    }

    private fun updateVideoUrl(country : String = this.countryKey) {
        if (VN_CONFIG.hasCountry(country)) {
            this.videoURL = videoUrlVn
        } else if (AMERICA_CONFIG.hasCountry(country)) {
            this.videoURL = videoUrlAmerica
        } else if (EU_CONFIG.hasCountry(country)) {
            this.videoURL = videoUrlEu
        } else if (ASIA_CONFIG.hasCountry(country)) {
            this.videoURL = videoUrlAsia
        } else {
            this.videoURL = videoUrlGlobal
        }
    }

    fun updateNetworkType(downloadSpeed: Int, uploadSpeed: Int) {
        networkType = if (downloadSpeed < 150) NetworkType.SLOW
        else if (downloadSpeed in 150..550) NetworkType.MEDIUM
        else NetworkType.FAST
        Log.d(TAG, "DETECT_NETWORK_TYPE::download: $downloadSpeed Kbps, upload: $uploadSpeed Kbps, Type network detected: ${networkType.value}")
    }

    fun assignCountry(country : String) {
        Log.d(TAG, "CountryCode: $country")
        this.countryKey = country
    }
}