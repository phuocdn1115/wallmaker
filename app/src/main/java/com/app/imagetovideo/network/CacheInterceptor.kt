package com.app.imagetovideo.network

import android.content.Context
import com.blankj.utilcode.util.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CacheInterceptor(private val mContext: Context) : Interceptor {
    private var durationCache = 60 * 60 * 24 * 7 // 1 week
    private var cacheAge = 120 // 1 minutes

    fun setDurationCache(durationCache: Int) {
        this.durationCache = durationCache
    }

    fun setCacheAge(cacheAge: Int) {
        this.cacheAge = cacheAge
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        if (request.method == "GET") {
            request = if (NetworkUtils.isConnected()) {
                request.newBuilder()
                    .header("Cache-Control", "public, max-age=$cacheAge")
                    .build()
            } else {
                request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$durationCache")
                    .build()
            }
        }

        return chain.proceed(request)
    }
}