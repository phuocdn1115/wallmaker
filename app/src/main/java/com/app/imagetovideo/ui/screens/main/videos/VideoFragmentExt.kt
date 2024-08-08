package com.app.imagetovideo.ui.screens.main.videos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.app.imagetovideo.R
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.ui.dialog.DialogRequestPermissionStorage
import com.app.imagetovideo.utils.ToastUtil

fun VideosFragment.checkPermissionReadExternalStorage(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


fun VideosFragment.showDialogRequestPermissionStorage() {
    requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
        ToastUtil.showToast(
            resources.getString(R.string.txt_explain_permission_storage),
            requireContext()
        )
    }, onClickButtonRequestPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                )
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        }

    })
    requestPermissionStorageDialog?.show(childFragmentManager, "DIALOG_REQUEST_PERMISSION")
}