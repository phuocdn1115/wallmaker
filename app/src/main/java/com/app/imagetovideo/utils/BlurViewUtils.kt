package com.app.imagetovideo.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

object BlurViewUtils {
    fun setupBlurView(activity: Activity,blurView: BlurView, viewGroup: ViewGroup? = null){
        val decorView = activity.window?.decorView
        if(viewGroup == null){
            val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
            val windowBackground = decorView.background
            blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(activity))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true)
        }
        else{
            val windowBackground = decorView?.background
            blurView.setupWith(viewGroup)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(activity))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true)
        }
    }
}