package com.app.photomaker.config

import com.app.photomaker.utils.FileUtils
import okhttp3.Interceptor
import java.io.File
import java.util.concurrent.ExecutorService

class GlobalConfigModule private constructor(builder: Builder) {
    private val mInterceptors: MutableList<Interceptor>?
    private val mCacheFile: File?
    private val mExecutorService: ExecutorService?

    init {
        this.mInterceptors = builder.mInterceptors
        this.mCacheFile = builder.mCacheFile
        this.mExecutorService = builder.mExecutorService
    }

    fun provideInterceptors(): MutableList<Interceptor>? {
        return mInterceptors
    }

    fun provideCacheFile(): File {
        return mCacheFile ?: FileUtils.getCacheFile()
    }


    class Builder {
        var mInterceptors: MutableList<Interceptor>? = null
        var mCacheFile: File? = null
        var mExecutorService: ExecutorService? = null


        fun addInterceptor(interceptor: Interceptor): Builder {
            if (mInterceptors == null) {
                mInterceptors = ArrayList()
            }
            (this.mInterceptors as ArrayList<Interceptor>).add(interceptor)
            return this
        }
        fun executorService(executorService: ExecutorService): Builder {
            this.mExecutorService = executorService
            return this
        }

        fun build(): GlobalConfigModule {
            return GlobalConfigModule(this)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}