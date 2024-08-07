package com.app.imagetovideo.startup

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.startup.Initializer
import com.app.imagetovideo.BuildConfig
import com.app.imagetovideo.PreferencesKey.COUNTRY_CODE
import com.app.imagetovideo.aplication.ApplicationContext
import com.blankj.utilcode.util.NetworkUtils
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.util.*

class CountryCodeInitializer: Initializer<Unit> {
    private val TAG = CountryCodeInitializer::class.simpleName
    override fun create(context: Context) {
        Log.i(TAG, "startup::CountryCodeInitializer")
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (!NetworkUtils.isConnected()) return

        val url = BuildConfig.COUNTRY_CODE_URL.toHttpUrl().newBuilder().addQueryParameter("lang", Locale.getDefault().language).build()
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val country = response.body?.string()
                    val newCountry = country?.replace("\n","") ?: Locale.getDefault().country
                    mPrefs.edit().putString(COUNTRY_CODE, newCountry).apply()
                    ApplicationContext.getNetworkContext().assignCountry(newCountry)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("CountryCode API execute failed.")
                val newCountry = Locale.getDefault().country
                ApplicationContext.getNetworkContext().assignCountry(newCountry)
            }
        })
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}