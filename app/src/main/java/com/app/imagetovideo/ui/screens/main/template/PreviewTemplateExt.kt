package com.app.imagetovideo.ui.screens.main.template

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.app.imagetovideo.R
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.ui.dialog.DialogRequestPermissionStorage
import com.app.imagetovideo.utils.ToastUtil

fun PreviewTemplateActivity.checkPermissionReadExternalStorage(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

fun PreviewTemplateActivity.showDialogRequestPermissionStorage() {
    requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), this)
    }, onClickButtonRequestPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        }

    })
    requestPermissionStorageDialog?.show(supportFragmentManager, "DIALOG_REQUEST_PERMISSION")
}

fun NewPreviewTemplateActivity.checkPermissionReadExternalStorage(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

fun NewPreviewTemplateActivity.showDialogRequestPermissionStorage() {
    requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), this)
    }, onClickButtonRequestPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        }
    })
    requestPermissionStorageDialog?.show(supportFragmentManager, "DIALOG_REQUEST_PERMISSION")
}