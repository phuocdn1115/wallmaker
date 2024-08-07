package com.app.imagetovideo.config

import android.content.Context
import com.app.imagetovideo.delegate.AppLifecycle

interface ConfigModule {

    fun applyOptions(context: Context, builder: GlobalConfigModule.Builder)

    fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycle>)
}