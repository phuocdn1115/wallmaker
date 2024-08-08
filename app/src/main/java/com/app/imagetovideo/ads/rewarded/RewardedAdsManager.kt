package com.app.imagetovideo.ads.rewarded

import android.app.Activity
import android.content.Context
import android.util.Log
import com.app.imagetovideo.aplication.ApplicationContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardedAdsManager @Inject constructor(
    private val context: Context
) {
    private val TAG = RewardedAdsManager::class.simpleName
    private val rewardAdQueue: Queue<RewardedAd> = LinkedList()
    private var activityName : String = ""
    var onAdDismissedFullScreenContentCallBack = object :()->Unit{
        override fun invoke() {
        }
    }

    private val fullScreenCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "LOAD_ADS_REWARDED::Ads rewarded show success")
            loadRewardedAds(ApplicationContext.getAdsContext().retry)
            onAdDismissedFullScreenContentCallBack.invoke()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.d(TAG, "LOAD_ADS_REWARDED::Ads rewarded show failure: $adError.")
            loadRewardedAds(ApplicationContext.getAdsContext().retry)
        }

        override fun onAdShowedFullScreenContent() {
            Log.d(TAG, "LOAD_ADS_REWARDED::Ads rewarded showing...")
        }
    }

    fun loadRewardedAds(retry: Int) {
        //return when AdUnit is empty, in the case reduce requests amount to admob account
        if (ApplicationContext.getAdsContext().adsRewardInPreviewId.isEmpty()) return
        var mRetry = retry
        Log.d(TAG, "LOAD_ADS_REWARDED::Ads rewarded start loading time: $mRetry")
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            ApplicationContext.getAdsContext().adsRewardInPreviewId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "LOAD_ADS_REWARDED::Ads rewarded load success")
                    if (rewardAdQueue.size < ApplicationContext.getAdsContext().rewardQueueSize) rewardAdQueue.add(rewardedAd)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    if (mRetry != 0) loadRewardedAds(--mRetry)
                }
            })
    }

    fun show(activity: Activity, onCallBack: (rewardItem: RewardItem?) -> Unit) {
        if (rewardAdQueue.isNotEmpty()) {
            val rewardedAd: RewardedAd? = rewardAdQueue.poll()
            activityName = activity.javaClass.simpleName
            rewardedAd?.fullScreenContentCallback = fullScreenCallback
            rewardedAd?.show(activity) {
                onCallBack.invoke(it)
            }
        }
        else {
            loadRewardedAds(ApplicationContext.getAdsContext().retry)
            onCallBack.invoke(null)
        }
    }

    fun setOnAdDismissedFullScreenContentListener(onDismissRewardAdCallBack:()->Unit){
        onAdDismissedFullScreenContentCallBack = onDismissRewardAdCallBack
    }

}