package com.app.photomaker.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.app.photomaker.R
import com.app.photomaker.base.BaseDialog
import com.app.photomaker.databinding.FragmentRequestPermissionReadAndWriteStorageBinding

class DialogRequestPermissionStorage(
    private val onClickButtonRequestPermission: () -> Unit,
    private val onClickButtonLater: () -> Unit
) : BaseDialog<FragmentRequestPermissionReadAndWriteStorageBinding>() {

    override fun getGravityForDialog(): Int = Gravity.BOTTOM

    override fun getLayoutResource(): Int = R.layout.fragment_request_permission_read_and_write_storage

    override fun init(saveInstanceState: Bundle?, view: View?) {}

    override fun setUp(view: View?) {
       binding.btnRequestPermission.setOnClickListener {
           onClickButtonRequestPermission.invoke()
       }
        binding.btnLater.setOnClickListener {
            onClickButtonLater.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        setFullScreenStatusBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }
}