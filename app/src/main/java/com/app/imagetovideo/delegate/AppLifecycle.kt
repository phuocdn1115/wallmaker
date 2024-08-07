package com.app.imagetovideo.delegate

import android.app.Application
import android.content.Context
import android.content.res.Configuration

interface AppLifecycle {

    fun attachBaseContext(context: Context)

    fun onCreate(application: Application)

    fun onTerminate(application: Application)

    fun onConfigurationChanged(configuration: Configuration)

    fun onLowMemory()

    fun onTrimMemory(level: Int)
}