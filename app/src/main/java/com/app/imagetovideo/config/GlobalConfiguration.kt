package com.app.imagetovideo.config

import android.content.Context
import com.app.imagetovideo.delegate.AppLifecycle
import com.app.imagetovideo.delegate.AppLifecycleImpl

class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycle>) {
        lifecycles.add(AppLifecycleImpl())
    }

}