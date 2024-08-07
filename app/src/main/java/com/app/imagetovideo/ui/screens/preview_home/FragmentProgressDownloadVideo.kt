package com.app.imagetovideo.ui.screens.preview_home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isInvisible
import com.app.imagetovideo.R
import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.base.BaseDialog
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.databinding.LayoutFragmentProgressSavingVideoBinding
import com.app.imagetovideo.ext.CoroutineExt
import com.app.imagetovideo.utils.BlurViewUtils
import com.app.imagetovideo.utils.extension.heightScreen
import com.app.imagetovideo.utils.extension.isHidden
import com.app.imagetovideo.utils.extension.widthScreen
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentProgressDownloadVideo() : BaseDialog<LayoutFragmentProgressSavingVideoBinding>() {

    @Inject
    lateinit var nativeAdsInFrameSaving: NativeAdsInFrameSaving

    override fun getLayoutResource(): Int = R.layout.layout_fragment_progress_saving_video

    override fun init(saveInstanceState: Bundle?, view: View?) {
        this.isCancelable = false
        binding.container.post {
            val heightCurrent = binding.container.height
            val widthSet = (heightCurrent * widthScreen) / heightScreen
            val layoutParamCurrent = binding.container.layoutParams
            layoutParamCurrent.width = widthSet
            binding.container.layoutParams = layoutParamCurrent
        }
        BlurViewUtils.setupBlurView(requireActivity(), binding.blurView, binding.imgThumb.rootView as ViewGroup)
        val adView = binding.nativeAdView

        /** Set other ad assets. */
        val data = nativeAdsInFrameSaving.getNativeAd()?.nativeAd
        adView.mediaView = binding.adMedia
        adView.headlineView = binding.adHeadline
        adView.callToActionView = binding.adCallToAction
        adView.iconView = binding.adAppIcon
        adView.starRatingView = binding.adStars
        adView.advertiserView = binding.adAdvertiser

        if (data == null) {
            binding.rootView.visibility = View.GONE
        } else {
            binding.rootView.visibility = View.VISIBLE
            (adView.headlineView as? TextView)?.text = data.headline
            if (data.callToAction == null) {
                (adView.callToActionView as Button).isHidden = true
            } else {
                (adView.callToActionView as Button).isHidden = false
                (adView.callToActionView as Button).text = data.callToAction
            }
            if (data.icon == null) {
                (adView.iconView as ShapeableImageView).isHidden = true
            } else {
                (adView.iconView as? ImageView)?.setImageDrawable(data.icon?.drawable)
                (adView.iconView as ShapeableImageView).isHidden = false
            }
            if (data.starRating == null) {
                (adView.starRatingView as RatingBar).visibility = View.GONE
            } else {
                (adView.starRatingView as RatingBar).rating = data.starRating?.toFloat() ?: 0F
                (adView.starRatingView as RatingBar).visibility = View.VISIBLE
            }
            if (data.advertiser == null) {
                (adView.advertiserView as TextView).isInvisible = true
            } else {
                (adView.advertiserView as TextView).text = data.advertiser
                (adView.advertiserView as TextView).isInvisible = false
            }
            adView.setNativeAd(data)
        }
    }

    fun setupPercent(progress: Int, max: Long) {
        CoroutineExt.runOnMain {
            binding.progressBarSaveVideo.progress = progress
            binding.progressBarSaveVideo.max = max.toInt()
            binding.tvProgress.text = "$progress %"
        }
    }

    fun setupThumb(url: String) {
        GlideHandler.setImageFormUrlWithCallBack(binding.imgThumb, url) {
            binding.progressBarSaveVideo.progress = 0
            binding.progressBarSaveVideo.visibility = View.VISIBLE
        }
    }

    override fun setUp(view: View?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }

    override fun getGravityForDialog(): Int = Gravity.BOTTOM

    override fun onStart() {
        super.onStart()
        setFullScreenDialog()
    }
}