package com.app.imagetovideo.ads.banner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.databinding.LayoutBannerAdsBinding
import com.google.android.gms.ads.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerAdsManager @Inject constructor(
    private val context: Context
) {
    val TAG = BannerAdsManager::class.simpleName
    private var parentViewAds: LayoutBannerAdsBinding? = null
    private val bannerAdQueue: Queue<AdView> = LinkedList()

    @SuppressLint("MissingPermission")
    fun loadAdsBanner(parentViewAds: LayoutBannerAdsBinding) {
        this.parentViewAds = parentViewAds
        try {
            val mAdView = AdView(context)
//            mAdView.adSize  = AdSize(AdSize.FULL_WIDTH, 50)
            mAdView.adUnitId = ApplicationContext.getAdsContext().adsBannerId
            mAdView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d(TAG, "onAdFailedToLoad: ${loadAdError.message}")
                    loadAdsBanner(parentViewAds)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d(TAG, "onAdLoaded: $mAdView")
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