package com.app.imagetovideo

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class WallpaperMakerApp: Application() {
    @Inject
    lateinit var okHttpClient : OkHttpClient

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: WallpaperMakerApp
        fun getContext(): Context = instance.applicationContext
        fun getOkHttpClient() = instance.okHttpClient
    }
}