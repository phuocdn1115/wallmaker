package com.app.photomaker.ads.banner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_ADS_LOAD
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_ADS_SHOW
import com.alo.ringo.tracking.base_event.AdsType
import com.alo.ringo.tracking.base_event.StatusType
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.databinding.LayoutBannerAdsBinding
import com.app.photomaker.tracking.EventTrackingManager
import com.google.android.gms.ads.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerAdsManager @Inject constructor(
    private val context: Context,
    private val eventTrackingManager: EventTrackingManager
) {
    val TAG = BannerAdsManager::class.simpleName
    private var parentViewAds: LayoutBannerAdsBinding? = null
    private val bannerAdQueue: Queue<AdView> = LinkedList()

    @SuppressLint("MissingPermission")
    fun loadAdsBanner(parentViewAds: LayoutBannerAdsBinding) {
        this.parentViewAds = parentViewAds
        try {
            val mAdView = AdView(context)
            mAdView.adSize = AdSize(AdSize.FULL_WIDTH, 50)
            mAdView.adUnitId = ApplicationContext.getAdsContext().adsBannerId
            mAdView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d(TAG, "onAdFailedToLoad: ${loadAdError.message}")
                    eventTrackingManager.sendAdsEvent(
                        eventName = EVENT_EV2_G1_ADS_LOAD,
                        contentId = ApplicationContext.getAdsContext().adsBannerId,
                        adsType = AdsType.BANNER.value,
                        isLoad = StatusType.FAIL.value
                    )
                    loadAdsBanner(parentViewAds)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    eventTrackingManager.sendAdsEvent(
                        eventName = EVENT_EV2_G1_ADS_SHOW,
                        contentId = ApplicationContext.getAdsContext().adsBannerId,
                        adsType = AdsType.BANNER.value,
                        isLoad = StatusType.SUCCESS.value,
                        show = StatusType.SUCCESS.value
                    )
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "onAdLoaded: $mAdView")
                    eventTrackingManager.sendAdsEvent(
                        eventName = EVENT_EV2_G1_ADS_LOAD,
                        contentId = ApplicationContext.getAdsContext().adsBannerId,
                        adsType = AdsType.BANNER.value,
                        isLoad = StatusType.SUCCESS.value
                    )
                    parentViewAds.layoutAd.removeAllViews()
                    parentViewAds.layoutAd.addView(mAdView)
                    if (bannerAdQueue.size < ApplicationContext.getAdsContext().bannerQueueSize)
                        bannerAdQueue.add(mAdView)
                }
            }
            mAdView.onPaidEventListener = OnPaidEventListener { adValue ->

            }
            mAdView.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {

        }
    }
}