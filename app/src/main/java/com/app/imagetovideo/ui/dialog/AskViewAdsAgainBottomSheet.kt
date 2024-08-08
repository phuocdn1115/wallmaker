package com.app.imagetovideo.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseDialog
import com.app.imagetovideo.databinding.LayoutRequestViewAdsAgainBottomSheetBinding
import com.app.imagetovideo.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskViewAdsAgainBottomSheet(private val onClickViewAds: () -> Unit) :
    BaseDialog<LayoutRequestViewAdsAgainBottomSheetBinding>() {


    override fun getLayoutResource(): Int = R.layout.layout_request_view_ads_again_bottom_sheet

    override fun init(saveInstanceState: Bundle?, view: View?) {}

    override fun setUp(view: View?) {
        binding.btnViewAds.setSafeOnClickListener {
            onClickViewAds.invoke()
            dismiss()
        }
        binding.btnNoViewAds.setSafeOnClickListener {
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun getGravityForDialog(): Int = Gravity.BOTTOM

    override fun onStart() {
        super.onStart()
        setSizeFullForDialog()
    }
}