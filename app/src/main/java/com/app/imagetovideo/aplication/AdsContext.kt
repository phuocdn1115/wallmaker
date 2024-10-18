package com.app.imagetovideo.aplication

import com.app.imagetovideo.BuildConfig
import com.blankj.utilcode.util.NetworkUtils

class AdsContext {
    var nativeQueueSize: Int = 1
    var rewardQueueSize: Int = 2

    var adsNativeInSetSuccessId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_NATIVE_SET_SUCCESS_ID
    var adsRewardInPreviewId: String = if (!NetworkUtils.isConnected()) "" else BuildConfig.ADS_REWARDED_IN_PREVIEW_ID


    //
    var retry: Int = 5
    var isLoadAds: Boolean = false
}