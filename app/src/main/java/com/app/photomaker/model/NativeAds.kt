package com.app.photomaker.model

import com.app.photomaker.model.Data
import com.google.android.gms.ads.nativead.NativeAd

class NativeAds : Data() {

    var viewed = 0
    var nativeAd: NativeAd?= null

    fun incrementView() = viewed++
}