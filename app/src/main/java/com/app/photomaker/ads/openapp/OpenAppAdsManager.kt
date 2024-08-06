package com.app.photomaker.ads.openapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_ADS_LOAD
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_ADS_SHOW
import com.alo.ringo.tracking.base_event.AdsType
import com.alo.ringo.tracking.base_event.StatusType
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.tracking.EventTrackingManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAppAdsManager @Inject constructor(
    private val context: Context,
    private val eventTrackingManager: EventTrackingManager
) {
    private val TAG = OpenAppAdsManager::class.simpleName
    private var currentActivity: Activity? = null
    private var appOpenAd: AppOpenAd? = null
    private var loadTime: Long = 0
    private var isLoadingAd = false
    private var isShowingAd = false
    private var isTurnOn = true

    private var defaultLifecycleObserver = object : DefaultLifecycleObserver {
        /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            currentActivity?.let { if (isTurnOn) showOpenAppAds(it) }
        }
    }

    private var activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityStarted(activity: Activity) {
            if (!isShowingOpenAppAds()) currentActivity = activity
        }

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    init {
        WallpaperMakerApp.instance.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        ProcessLifecycleOwner.get().lifecycle.addObserver(defaultLifecycleObserver)
    }

    fun loadOpenAppAds(retry: Int) {

        //return when AdUnit is empty, in the case reduce requests amount to admob account
        if (ApplicationContext.getAdsContext().adsOpenAdsId.isEmpty()) return

        if (isLoadingAd || isAdAvailable()) return
        var mRetry = retry
        Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app start loading time: $mRetry")
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context,
            ApplicationContext.getAdsContext().adsOpenAdsId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app load success")
                    eventTrackingManager.sendAdsEvent(
                        eventName = EVENT_EV2_G1_ADS_LOAD,
                        contentId = ApplicationContext.getAdsContext().adsOpenAdsId,
                        adsType = AdsType.OPEN.value,
                        isLoad = StatusType.SUCCESS.value
                    )
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoadingAd = false
                    Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app load failure. \n Reason: $error")
                    eventTrackingManager.sendAdsEvent(
                        eventName = EVENT_EV2_G1_ADS_LOAD,
                        contentId = ApplicationContext.getAdsContext().adsOpenAdsId,
                        adsType = AdsType.OPEN.value,
                        isLoad = StatusType.FAIL.value
                    )
                    if (mRetry != 0) loadOpenAppAds(--mRetry)
                }
            })
    }

    fun showOpenAppAds(activity: Activity) {
        if (isShowingAd) {
            Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app is already showing.")
            return
        }

        /** If the app open ad is not available yet, invoke the callback then load the ad. */
        if (!isAdAvailable()) {
            loadOpenAppAds(ApplicationContext.getAdsContext().retry)
            return
        }

        Log.d(TAG, "Ads open app will show.")
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app dismissed.")
                loadOpenAppAds(ApplicationContext.getAdsContext().retry)
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app show error: " + adError.message)
                eventTrackingManager.sendAdsEvent(
                    eventName = EVENT_EV2_G1_ADS_SHOW,
                    contentId = ApplicationContext.getAdsContext().adsOpenAdsId,
                    adsType = AdsType.OPEN.value,
                    isLoad = StatusType.SUCCESS.value,
                    show = StatusType.FAIL.value
                )
                loadOpenAppAds(ApplicationContext.getAdsContext().retry)
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "LOAD_ADS_OPEN_APP::Ads open app showing.")
                eventTrackingManager.sendAdsEvent(
                    eventName = EVENT_EV2_G1_ADS_SHOW,
                    contentId = ApplicationContext.getAdsContext().adsOpenAdsId,
                    adsType = AdsType.OPEN.value,
                    isLoad = StatusType.SUCCESS.value,
                    show = StatusType.SUCCESS.value
                )
            }
        }
        isShowingAd = true
        appOpenAd?.show(activity)
    }

    fun isShowingOpenAppAds(): Boolean {
        return isShowingAd
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    fun switchOnOff(turnOn: Boolean) {
        isTurnOn = turnOn
    }
}