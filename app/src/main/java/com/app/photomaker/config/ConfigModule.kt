package com.app.photomaker.config

import android.content.Context
import com.app.photomaker.delegate.AppLifecycle

interface ConfigModule {

    fun applyOptions(context: Context, builder: GlobalConfigModule.Builder)

    fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycle>)
}