package com.app.photomaker.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_REWARD
import com.alo.ringo.tracking.base_event.AdsRewardType
import com.alo.ringo.tracking.base_event.StatusType
import com.app.photomaker.R
import com.app.photomaker.aplication.ApplicationContext.getAdsContext
import com.app.photomaker.base.BaseDialog
import com.app.photomaker.databinding.LayoutRequestViewAdsAgainBottomSheetBinding
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AskViewAdsAgainBottomSheet(private val onClickViewAds: () -> Unit) :
    BaseDialog<LayoutRequestViewAdsAgainBottomSheetBinding>() {

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    override fun getLayoutResource(): Int = R.layout.layout_request_view_ads_again_bottom_sheet

    override fun init(saveInstanceState: Bundle?, view: View?) {}

    override fun setUp(view: View?) {
        binding.btnViewAds.setSafeOnClickListener {
            eventTrackingManager.sendRewardAdsEvent(
                eventName = EVENT_EV2_G1_REWARD,
                contentId = getAdsContext().adsRewardInPreviewId,
                inPopup = AdsRewardType.EXPLAIN_ADS.value,
                approve = StatusType.SUCCESS.value,
                status = StatusType.EMPTY.value
            )
            onClickViewAds.invoke()
            dismiss()
        }
        binding.btnNoViewAds.setSafeOnClickListener {
            eventTrackingManager.sendRewardAdsEvent(
                eventName = EVENT_EV2_G1_REWARD,
                contentId = getAdsContext().adsRewardInPreviewId,
                inPopup = AdsRewardType.EXPLAIN_ADS.value,
                approve = StatusType.FAIL.value,
                status = StatusType.FAIL.value
            )
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