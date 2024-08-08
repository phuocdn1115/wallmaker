package com.app.imagetovideo.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseDialog
import com.app.imagetovideo.databinding.LayoutRequestViewAdsBottomSheetBinding
import com.app.imagetovideo.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskViewAdsBottomSheet(private val onClickViewAds: () -> Unit, private val onDismiss: () -> Unit) :
    BaseDialog<LayoutRequestViewAdsBottomSheetBinding>() {

    override fun getLayoutResource(): Int = R.layout.layout_request_view_ads_bottom_sheet

    override fun init(saveInstanceState: Bundle?, view: View?) {
        this.isCancelable = false
    }

    override fun setUp(view: View?) {
        binding.btnViewAds.setSafeOnClickListener {
            onClickViewAds.invoke()
            dismiss()
        }
        binding.btnNoViewAds.setSafeOnClickListener {
            onDismiss.invoke()
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