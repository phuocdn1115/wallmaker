package com.app.imagetovideo.ui.screens.edit_screen.set_wallpaper

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.app.imagetovideo.R
import com.app.imagetovideo.WallpaperMakerApp
import com.app.imagetovideo.base.handler.WallpaperHandler
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.enums.WallpaperTypeSetting
import com.app.imagetovideo.live.LiveWallpaperService
import com.app.imagetovideo.utils.ToastUtil
import com.app.imagetovideo.utils.extension.decodeBitmap
import com.app.imagetovideo.base.Result

fun SetWallpaperFragment.setLiveWallpaper() {
    if (WallpaperMakerApp.instance.packageManager.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER)) {
        editorVM.saveWallpaperVideoUrl(absolutePathFile)
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            ComponentName(WallpaperMakerApp.instance, LiveWallpaperService::class.java)
        )
        startActivityForResult(intent, RequestCode.SET_WALLPAPER_LIVE.requestCode)
    } else ToastUtil.showToast(resources.getString(R.string.txt_set_wallpaper_error), requireContext())
}

fun SetWallpaperFragment.renameFile() {
    objectImageVideoCreated?.name = binding.tvNameWallpaper.text.toString().trim()
    editorVM.renameImageVideoUserCreated(objectImageVideoCreated)
}

fun SetWallpaperFragment.showChooseTypeSettingBottomSheetDialog() {
    chooseTypeSettingBottomSheet?.show(childFragmentManager, "chooseTypeSettingBottomSheet")
}

fun SetWallpaperFragment.showSetVideoSuccessDialog() {
    setVideoWallpaperSuccessDialog?.show(childFragmentManager, "dialogSetVideoWallpaperSuccess")
}

fun SetWallpaperFragment.showSetImageSuccessDialog() {
    setImageWallpaperSuccessDialog?.show(childFragmentManager, "dialogSetImageWallpaperSuccess")
}

fun SetWallpaperFragment.setWallpaper(path: String, typeSetting: WallpaperTypeSetting) {
    val setWallpaperLiveData = MutableLiveData<Result<Boolean>>()
    setWallpaperLiveData.postValue(Result.InProgress())
    val bitmap = decodeBitmap(path)
    var postValue = false
    if (bitmap != null) WallpaperHandler.setWallpaper(bitmap, typeSetting) { isSuccess ->
        if (isSuccess && !postValue) {
            setWallpaperLiveData.postValue(Result.Success(true))
            postValue = true
            showSetImageSuccessDialog()
        } else {
            ToastUtil.showToast(resources.getString(R.string.txt_set_wallpaper_error), requireContext())
            setWallpaperLiveData.postValue(Result.Failure(400, "Error, Try again!"))
        }
    }
}