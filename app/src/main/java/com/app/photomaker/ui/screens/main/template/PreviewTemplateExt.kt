package com.app.photomaker.ui.screens.main.template

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.photomaker.R
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.utils.ToastUtil

fun PreviewTemplateActivity.checkPermissionReadExternalStorage(): Boolean =
    PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

fun PreviewTemplateActivity.showDialogRequestPermissionStorage() {
    requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), this)
    }, onClickButtonRequestPermission = {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
        )
    })
    requestPermissionStorageDialog?.show(supportFragmentManager, "DIALOG_REQUEST_PERMISSION")
}