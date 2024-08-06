package com.app.photomaker.viewholder.videohome

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.photomaker.R
import com.app.photomaker.databinding.ItemNativeAdsVideoHomeBinding
import com.app.photomaker.utils.extension.isHidden
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.imageview.ShapeableImageView

class NativeAdsViewHolder(itemBinding: ItemNativeAdsVideoHomeBinding, context: Context) :
    RecyclerView.ViewHolder(itemBinding.root) {
    val binding: ItemNativeAdsVideoHomeBinding
    val mcontext: Context

    init {
        this.binding = itemBinding
        mcontext = context
        setupUIItemImgVideoHome(itemView)
    }

    fun bind(data: NativeAd?){
        if(data == null){
            removeAdsView()
        } else {
            showAdsView(data)
        }
    }

    private fun showAdsView(data: NativeAd) {
        val adView = binding.nativeAdView
        adView.mediaView = binding.adMedia
        adView.headlineView = binding.adHeadline
        adView.callToActionView = binding.adCallToAction
        adView.iconView = binding.adAppIcon

        (adView.headlineView as TextView).text = data.headline

        if(data.callToAction == null){
            (adView.callToActionView as Button).isHidden = true
        } else {
            (adView.callToActionView as Button).isHidden = false
            (adView.callToActionView as Button).text = mcontext.resources.getString(R.string.text_setting_btn_native_ads_video)
            (adView.callToActionView as Button).typeface = mcontext.resources.getFont(R.font.beviet_bold)
            (adView.callToActionView as Button).isAllCaps = false


        }

        if(data.icon == null){
            (adView.iconView as ShapeableImageView).isHidden = true
        } else {
            (adView.iconView as ShapeableImageView)?.setImageDrawable(data?.icon.drawable)
            (adView.iconView as ShapeableImageView).isHidden = false
        }

        adView.starRatingView = binding.adStars

        if (data.starRating == null) {
            (adView.starRatingView as RatingBar).visibility = View.GONE
        } else {

            (adView.starRatingView as RatingBar).rating = data.starRating?.toFloat() ?: 0F
            (adView.starRatingView as RatingBar).visibility = View.VISIBLE
        }

        adView.setNativeAd(data)

        val vc = data.mediaContent

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc?.hasVideoContent() == true) {
            binding.adMedia.minimumHeight = 120
        } else {
            binding.adMedia.minimumHeight = 100
        }
    }

    private fun removeAdsView() {
        val layoutParams = binding.root.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.height = 0
        layoutParams.setMargins(0,0,0,0)
    }
}