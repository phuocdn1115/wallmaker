package com.app.imagetovideo.repository

import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.ads.nativeads.NativeAdsInHomeManager
import com.app.imagetovideo.ads.nativeads.NativeAdsSetSuccessManager
import com.app.imagetovideo.ads.openapp.OpenAppAdsManager
import com.app.imagetovideo.ads.rewarded.RewardedAdsManager
import com.app.imagetovideo.aplication.ApplicationContext
import javax.inject.Inject

class AdvertiseRepository @Inject constructor(
    private val nativeAdsInHomeManager: NativeAdsInHomeManager,
    private val nativeAdsSetSuccessManager: NativeAdsSetSuccessManager,
    private val nativeAdsInFrameSaving: NativeAdsInFrameSaving,
    private val rewardedAdsManager: RewardedAdsManager,
    private val openAppAdsManager: OpenAppAdsManager
) {
    fun loadAds() {
        openAppAdsManager.loadOpenAppAds(ApplicationContext.getAdsContext().retry)
        nativeAdsInHomeManager.loadNativeAd(ApplicationContext.getAdsContext().retry)
        nativeAdsSetSuccessManager.loadNativeAd(ApplicationContext.getAdsContext().retry)
        nativeAdsInFrameSaving.loadNativeAd(ApplicationContext.getAdsContext().retry)
        rewardedAdsManager.loadRewardedAds(ApplicationContext.getAdsContext().retry)
    }
}