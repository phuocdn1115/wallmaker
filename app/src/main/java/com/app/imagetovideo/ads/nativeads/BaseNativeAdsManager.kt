package com.app.imagetovideo.ads.nativeads

import android.content.Context
import android.util.Log
import com.app.imagetovideo.model.NativeAds
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

abstract class BaseNativeAdsManager {
    abstract val TAG: String
    abstract val context: Context
    abstract val nativeAdsQueueSize: Int
    abstract val nativeAdQueue: Queue<NativeAds>
    abstract fun adsNativeId(): String


    private var disposableNativeAds: Disposable? = null

    fun loadNativeAd(retry: Int) {
        if (retry < 0) return
        var mRetry = retry
        Log.d(TAG, "Ads native start loading time: $mRetry")
        disposableNativeAds?.dispose()
        disposableNativeAds = singleNativeAdsTask()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                {
                    if (nativeAdQueue.size < nativeAdsQueueSize) {
                        loadNativeAd(--mRetry)
                    }
                }
            )
    }

    private fun singleNativeAdsTask(): Single<NativeAd> {
        return Single.create { emitter ->
            val adLoaderBuilder = AdLoader.Builder(context,adsNativeId())
            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
            val nativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
            adLoaderBuilder
                .forNativeAd {
                    Log.d(TAG, "Ads native load success: ${it.headline}")
                    val nativeAdRemote = NativeAds()
                    nativeAdRemote.nativeAd = it
                    nativeAdQueue.add(nativeAdRemote)
                    if (!emitter.isDisposed) { emitter.onSuccess(it) }
                }
                .withNativeAdOptions(nativeAdOptions)
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d(TAG, "Ads native load failure. \n Reason: $error")
                        if (!emitter.isDisposed) {
                            emitter.onError(Exception())
                        }
                    }

                    override fun onAdLoaded() {
                        Log.d(TAG, "Ads native load success")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        Log.d(TAG, "Ads native Impression")
                    }
                }).build()
                .loadAd(AdManagerAdRequest.Builder().build())
        }
    }
}