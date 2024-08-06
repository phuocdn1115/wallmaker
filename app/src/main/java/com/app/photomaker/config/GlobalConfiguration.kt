package com.app.photomaker.config

import android.content.Context
import com.app.photomaker.delegate.AppLifecycle
import com.app.photomaker.delegate.AppLifecycleImpl

class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<AppLifecycle>) {
        lifecycles.add(AppLifecycleImpl())
    }

}