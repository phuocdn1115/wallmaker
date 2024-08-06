package com.app.photomaker.ui.screens.main.videos

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.photomaker.R
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.utils.ToastUtil

fun VideosFragment.checkPermissionReadExternalStorage(): Boolean =
    PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

fun VideosFragment.showDialogRequestPermissionStorage(){
    requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), requireContext())
    }, onClickButtonRequestPermission = {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
        )
    })
    requestPermissionStorageDialog?.show(childFragmentManager, "DIALOG_REQUEST_PERMISSION")
}