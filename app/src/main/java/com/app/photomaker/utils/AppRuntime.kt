package com.app.photomaker.utils

import android.app.ActivityManager
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils

/**
 * Get system services
 */
inline fun <reified T> getSystemService(): T? =
    ContextCompat.getSystemService(Utils.getApp(), T::class.java)

val activityManager get() = getSystemService<ActivityManager>()