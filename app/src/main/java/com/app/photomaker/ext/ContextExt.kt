package com.app.photomaker.ext

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.app.photomaker.utils.activityManager
import java.util.*

object ContextExt {

    fun Context.getMemory() : ActivityManager.MemoryInfo {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)
        return memInfo
    }

    val Context.memoryByGB :  Double get() = getMemory().totalMem / 1073741824.0

    fun Context.getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase(Locale.getDefault()).startsWith(manufacturer.lowercase(Locale.getDefault()))) {
            capitalize(model)
        } else {
            capitalize(manufacturer).toString() + " " + model
        }
    }

    private fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}