package com.app.imagetovideo.utils.glide

import android.content.Context
import android.os.Build
import com.bumptech.glide.manager.ConnectivityMonitor
import com.bumptech.glide.manager.ConnectivityMonitorFactory
import java.util.*

class NoConnectivityMonitorFactory : ConnectivityMonitorFactory {

    companion object {
        /**
         * Huawei 5.1 5.11 models need to disable the glide network monitoring function
         *
         * @return
         */
        fun isNeedDisableNetCheck(): Boolean {
            return isHuaWei() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        }

        private fun isHuaWei(): Boolean {
            val brand: String = Build.BRAND.lowercase(Locale.ROOT)
            return brand.contains("huawei") || brand.contains("honor")
        }
    }

    override fun build(
        context: Context,
        listener: ConnectivityMonitor.ConnectivityListener
    ): ConnectivityMonitor {
        return object : ConnectivityMonitor {
            override fun onStart() {
            }

            override fun onStop() {
            }

            override fun onDestroy() {
            }
        }
    }
}