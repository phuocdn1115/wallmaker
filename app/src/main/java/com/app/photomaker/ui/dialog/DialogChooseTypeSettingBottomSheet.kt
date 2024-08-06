package com.app.photomaker.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.photomaker.R
import com.app.photomaker.base.BaseDialog
import com.app.photomaker.databinding.LayoutChooseTypeSettingBottomSheetBinding
import com.app.photomaker.enums.WallpaperTypeSetting
import com.app.photomaker.utils.setSafeOnClickListener

class DialogChooseTypeSettingBottomSheet(
    private val onSettingImage: (WallpaperTypeSetting) -> Unit
) : BaseDialog<LayoutChooseTypeSettingBottomSheetBinding>() {

    override fun getLayoutResource(): Int = R.layout.layout_choose_type_setting_bottom_sheet

    override fun init(saveInstanceState: Bundle?, view: View?) {}

    override fun setUp(view: View?) {
        binding.btnHomeScreen.setSafeOnClickListener {
            dismiss()
            onSettingImage.invoke(WallpaperTypeSetting.HOME)
        }
        binding.btnLockScreen.setSafeOnClickListener {
            dismiss()
            onSettingImage.invoke(WallpaperTypeSetting.LOCK)
        }
        binding.btnAll.setSafeOnClickListener {
            dismiss()
            onSettingImage.invoke(WallpaperTypeSetting.BOTH)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun getGravityForDialog(): Int = Gravity.BOTTOM

    override fun onStart() {
        super.onStart()
        setSizeFullForDialog()
    }
}