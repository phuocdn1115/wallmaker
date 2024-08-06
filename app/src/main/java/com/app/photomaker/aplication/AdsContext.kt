package com.app.photomaker.aplication

import com.app.photomaker.BuildConfig
import com.blankj.utilcode.util.NetworkUtils

class AdsContext {
    var interQueueSize: Int = 2
    var interViewThreshold: Int = 1
    var nativeQueueSize: Int = 1
    var nativeViewThreshold: Int = 4
    var rewardQueueSize: Int = 2
    var rewardViewThreshold: Int = 1
    var bannerQueueSize: Int = 2

    var adsNativeInHomeId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_NATIVE_HOME_ID
    var adsNativeInFrameId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_NATIVE_IN_FRAME_ID
    var adsNativeInSetSuccessId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_NATIVE_SET_SUCCESS_ID
    var adsRewardInPreviewId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_REWARDED_IN_PREVIEW_ID
    var adsOpenAdsId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_OPEN_APP_ID
    var adsBannerId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_BANNER_ID


    //
    var retry: Int = 5
    var isLoadAds: Boolean = false
}