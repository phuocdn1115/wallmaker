package com.app.imagetovideo.ads.nativeads

import android.content.Context
import android.util.Log
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.model.NativeAds
import com.app.imagetovideo.tracking.EventTrackingManager
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdsSetSuccessManager @Inject constructor(
    mContext : Context,
    mEventTrackingManager: EventTrackingManager
) : BaseNativeAdsManager() {
    override val TAG: String = NativeAdsSetSuccessManager::class.simpleName.toString()
    override val context: Context = mContext
    override val nativeAdsQueueSize: Int = ApplicationContext.getAdsContext().nativeQueueSize
    override val nativeAdQueue: Queue<NativeAds> = LinkedList()
    override fun adsNativeId(): String = ApplicationContext.getAdsContext().adsNativeInSetSuccessId
    override val eventTrackingManager: EventTrackingManager = mEventTrackingManager

    private var nativeAdRemote: NativeAds? = null
    fun getNativeAd(): NativeAds? {
        Log.d(TAG, "\n--------------------------------------------------------Update native ads--------------------------------------------------------")
        nativeAdRemote?.nativeAd?.destroy()
        nativeAdRemote = nativeAdQueue.poll()
        loadNativeAd(ApplicationContext.getAdsContext().retry)
        Log.d(TAG, "Init current nativeAdRemote is: ${nativeAdRemote?.nativeAd?.responseInfo}, native Ad: ${nativeAdRemote?.nativeAd} \nName native ads is: ${nativeAdRemote?.nativeAd?.headline}")
        return nativeAdRemote
    }
}