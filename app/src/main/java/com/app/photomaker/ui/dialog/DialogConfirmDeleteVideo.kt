package com.app.photomaker.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.photomaker.R
import com.app.photomaker.base.BaseDialog
import com.app.photomaker.databinding.LayoutPopupConfirmDeleteVideoBinding
import com.app.photomaker.utils.setSafeOnClickListener

class DialogConfirmDeleteVideo (private val onClickDelete: () -> Unit) :
    BaseDialog<LayoutPopupConfirmDeleteVideoBinding>() {

    override fun getLayoutResource(): Int = R.layout.layout_popup_confirm_delete_video

    override fun init(saveInstanceState: Bundle?, view: View?) {}

    override fun setUp(view: View?) {
        binding.btnDelete.setSafeOnClickListener {
            onClickDelete.invoke()
            dismiss()
        }
        binding.btnCancel.setSafeOnClickListener {
            dismiss()
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