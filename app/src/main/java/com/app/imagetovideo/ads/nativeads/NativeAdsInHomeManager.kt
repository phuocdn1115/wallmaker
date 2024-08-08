package com.app.imagetovideo.ads.nativeads

import android.content.Context
import android.util.Log
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.model.NativeAds
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NativeAdsInHomeManager @Inject constructor(
    mContext : Context
) : BaseNativeAdsManager() {
    override val TAG: String = NativeAdsInHomeManager::class.simpleName.toString()
    override val context: Context = mContext
    override val nativeAdsQueueSize: Int = ApplicationContext.getAdsContext().nativeQueueSize
    override val nativeAdQueue: Queue<NativeAds> = LinkedList()
    override fun adsNativeId(): String = ApplicationContext.getAdsContext().adsNativeInHomeId

    private var nativeAdRemoteCurrent: NativeAds? = null
    fun getNativeAd(): NativeAds? {
        if (nativeAdRemoteCurrent == null || nativeAdRemoteCurrent?.viewed == ApplicationContext.getAdsContext().nativeViewThreshold) {
            Log.d(TAG, "\n--------------------------------------------------------Update native ads--------------------------------------------------------")
            nativeAdRemoteCurrent?.nativeAd?.destroy()
            nativeAdRemoteCurrent = nativeAdQueue.poll()
            loadNativeAd(ApplicationContext.getAdsContext().retry)
        }
        Log.d(TAG, "Init current nativeAdRemote is: ${nativeAdRemoteCurrent?.nativeAd?.responseInfo}, native Ad: ${nativeAdRemoteCurrent?.nativeAd} \nName native ads is: ${nativeAdRemoteCurrent?.nativeAd?.headline}")
        nativeAdRemoteCurrent?.incrementView()
        return nativeAdRemoteCurrent
    }
}