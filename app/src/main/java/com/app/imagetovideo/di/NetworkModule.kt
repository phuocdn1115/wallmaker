package com.app.imagetovideo.di
import com.app.imagetovideo.BuildConfig
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.ext.ContextExt.getDeviceName
import com.app.imagetovideo.ext.ContextExt.memoryByGB
import com.app.imagetovideo.network.Api
import com.app.imagetovideo.network.CacheInterceptor
import com.app.imagetovideo.network.addGoogleDns
import com.app.imagetovideo.utils.extension.getDeviceId
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object NetworkModule {

    @Provides
    @Reusable
    @JvmStatic
    internal fun providePostApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInterface(): Retrofit {
        val logging = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        val cacheInterceptor = CacheInterceptor(WallpaperMakerApp.getContext())
        val maxSize = 50L * 1024L * 1024L // 50 MiB
        val cache = Cache(File(WallpaperMakerApp.instance.cacheDir, "http_cache"), maxSize)

        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(20, TimeUnit.SECONDS)
        httpClient.readTimeout(20, TimeUnit.SECONDS)
        httpClient.writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging) // <-- this is the important line!
            .addNetworkInterceptor(cacheInterceptor)
            .cache(cache)
            .addGoogleDns()
            .callTimeout(8000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Connection", "close")
                    .addHeader("Accept", "application/json")
                    .addHeader("language", Locale.getDefault().language)
                    .addHeader("country", ApplicationContext.getNetworkContext().countryKey)
                    .addHeader("X-NetworkType", ApplicationContext.getNetworkContext().networkType.value)
                    .addHeader("X-DeviceModel", WallpaperMakerApp.getContext().getDeviceName())
                    .addHeader("X-DeviceMemory", WallpaperMakerApp.getContext().memoryByGB.toString())
                    .addHeader("X-AppVersion", BuildConfig.VERSION_NAME)
                    .addHeader("X-AppVersionCode", BuildConfig.VERSION_CODE.toString())
                    .addHeader("X-AppId", BuildConfig.APPLICATION_ID)
                    .addHeader("X-DeviceId", WallpaperMakerApp.getContext().getDeviceId())
                    .addHeader("X-DeviceName", WallpaperMakerApp.getContext().getDeviceName())
                    .addHeader("X-AppVersion", BuildConfig.VERSION_NAME)
                    .addHeader("Content-Encoding", "gzip")
                    .build()
                chain.proceed(request)
            }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.GLOBAL_API_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient.build())
            .build()
    }

    @Singleton
    @Provides
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addGoogleDns().build()
    }

}