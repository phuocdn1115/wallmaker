package com.app.photomaker.di

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.utils.CommonUtils
import com.app.photomaker.utils.FileUtils
import com.app.photomaker.utils.activityManager
import com.app.photomaker.utils.glide.NoConnectivityMonitorFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream

@GlideModule
class GlideModule: AppGlideModule() {

    @SuppressLint("CheckResult")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val appComponent = CommonUtils.getAppComponent()
        builder.setDiskCache {
            // Careful: the external cache directory doesn't enforce permissions
            DiskLruCacheWrapper.create(FileUtils.getGlideCacheFile(), IMAGE_DISK_CACHE_MAX_SIZE.toLong())
        }

        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize

        val customMemoryCacheSize = (1.2 * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize = (1.2 * defaultBitmapPoolSize).toInt()

        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))

        RequestOptions().apply {
            // Prefer higher quality images unless we're on a low RAM device
            format(if (activityManager?.isLowRamDevice == true) DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888)
            // Disable hardware bitmaps as they don't play nicely with Palette
            disallowHardwareConfig()
        }.let(builder::setDefaultRequestOptions)

        //Compatible with the problem of Register too many Broadcast Receivers on Huawei Tablet 5.1 and 5.0 models
        if (NoConnectivityMonitorFactory.isNeedDisableNetCheck()) {
            builder.setConnectivityMonitorFactory(NoConnectivityMonitorFactory())
        }

        //LOG DEBUG
        builder.setLogLevel(Log.DEBUG);
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,  OkHttpUrlLoader.Factory(
                WallpaperMakerApp.instance.okHttpClient))
    }

    /**
     * @return Set manifest parsing, set to false to avoid adding the same modules twice
     */
    override fun isManifestParsingEnabled() = false

    companion object {
        private const val IMAGE_DISK_CACHE_MAX_SIZE = 100 * 1024 * 1024//The maximum image cache file size is 100Mb
    }
}