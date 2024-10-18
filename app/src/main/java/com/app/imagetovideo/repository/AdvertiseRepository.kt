package com.app.imagetovideo.repository

import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.ads.nativeads.NativeAdsSetSuccessManager
import com.app.imagetovideo.ads.rewarded.RewardedAdsManager
import javax.inject.Inject

class AdvertiseRepository @Inject constructor(
    private val nativeAdsSetSuccessManager: NativeAdsSetSuccessManager,
    private val nativeAdsInFrameSaving: NativeAdsInFrameSaving,
    private val rewardedAdsManager: RewardedAdsManager,
) {
    fun loadAds() {
//        openAppAdsManager.loadOpenAppAds(ApplicationContext.getAdsContext().retry)
//        nativeAdsInHomeManager.loadNativeAd(ApplicationContext.getAdsContext().retry)
//        nativeAdsSetSuccessManager.loadNativeAd(ApplicationContext.getAdsContext().retry)
//        nativeAdsInFrameSaving.loadNativeAd(ApplicationContext.getAdsContext().retry)
//        rewardedAdsManager.loadRewardedAds(ApplicationContext.getAdsContext().retry)
    }
}