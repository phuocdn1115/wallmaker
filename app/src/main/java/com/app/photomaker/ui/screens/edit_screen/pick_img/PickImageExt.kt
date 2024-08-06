package com.app.photomaker.ui.screens.edit_screen.pick_img

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.photomaker.R
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.utils.ToastUtil

fun PickImageFragment.checkPermissionReadExternalStorage(): Boolean {
    return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
}

fun PickImageFragment.showDialogRequestPermissionStorage(){
    dialogGrantPermissionStorage = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), requireContext())
        activity?.finish()
    }, onClickButtonRequestPermission = {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
        )
    })
    dialogGrantPermissionStorage?.show(childFragmentManager, "DIALOG_REQUEST_PERMISSION")
}