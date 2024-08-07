package com.app.imagetovideo.ext

import android.view.View

object ViewExt {
    fun View.isVisible(isVisible : Boolean) {
        if (isVisible) this.visibility = View.VISIBLE
        else this.visibility = View.GONE
    }
}