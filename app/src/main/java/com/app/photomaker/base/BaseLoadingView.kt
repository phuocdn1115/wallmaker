package com.app.photomaker.base

import android.view.View
import com.app.photomaker.ext.CoroutineExt
import com.app.photomaker.ext.ViewExt.isVisible
import com.facebook.shimmer.ShimmerFrameLayout

interface BaseLoadingView {
    fun getLayoutParent() : View
    fun getLayoutShimmer() : ShimmerFrameLayout

    fun setupLoading(isLoading : Boolean, timeDelayLoading: Long, callback: (() -> Unit?)? = null ) {
        if (isLoading) {
            startLoading()
        }
        if (!isLoading) {
            callback?.invoke()
            CoroutineExt.runOnMainAfterDelay( if(timeDelayLoading > 0) timeDelayLoading + 500.toLong() else 500) { stopLoading(callback) }
        }
    }

    fun startLoading() {
        getLayoutParent().isVisible(true)
        getLayoutParent().animate().alpha(1F)
        getLayoutShimmer().apply {
            startShimmer()
            showShimmer(true)
        }
    }

    fun stopLoading(callback: (() -> Unit?)? = null) {
        getLayoutParent().animate().alpha(0F)
        getLayoutShimmer().apply {
            stopShimmer()
            hideShimmer()
            getLayoutParent().isVisible(false)
        }
    }
}