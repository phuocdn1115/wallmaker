package com.app.imagetovideo.aplication

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.app.imagetovideo.PreferencesKey.COUNTRY_CODE
import com.app.imagetovideo.enums.NetworkType
import com.blankj.utilcode.util.Utils
import java.util.Locale

class NetworkContext {

    val TAG = this.javaClass.simpleName
    val preferenceManager : SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(Utils.getApp().applicationContext)}
    var isNetworkConnected: Boolean = true
    var networkType = NetworkType.MEDIUM
    var countryKey = Locale.getDefault().country

    var bestUrl : String =  ""

    init {
        countryKey = preferenceManager.getString(COUNTRY_CODE, Locale.getDefault().country)
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