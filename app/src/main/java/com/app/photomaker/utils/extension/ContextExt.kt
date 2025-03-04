package com.app.photomaker.utils.extension

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun Context.getDeviceId(): String {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}