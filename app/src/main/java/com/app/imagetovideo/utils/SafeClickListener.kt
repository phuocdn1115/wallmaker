package com.app.imagetovideo.utils

import android.annotation.SuppressLint
import android.os.SystemClock
import android.view.View
import com.app.imagetovideo.ext.CoroutineExt
import com.thekhaeng.pushdownanim.PushDownAnim

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

@SuppressLint("ClickableViewAccessibility")
fun pushDownClickAnimation(scale: Float = 0.95f, view: View, callbackAction: () -> Unit) {
    PushDownAnim.setPushDownAnimTo(view)
        .setScale(PushDownAnim.MODE_SCALE, scale)
        .setOnClickListener {
            CoroutineExt.runOnMainAfterDelay(112) {
                callbackAction.invoke()
            }
        }
}
