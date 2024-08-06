package com.app.photomaker.repository

import com.app.photomaker.ads.nativeads.NativeAdsInFrameSaving
import com.app.photomaker.ads.nativeads.NativeAdsInHomeManager
import com.app.photomaker.ads.nativeads.NativeAdsSetSuccessManager
import com.app.photomaker.ads.openapp.OpenAppAdsManager
import com.app.photomaker.ads.rewarded.RewardedAdsManager
import com.app.photomaker.aplication.ApplicationContext
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