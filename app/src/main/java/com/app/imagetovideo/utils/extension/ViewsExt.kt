package com.app.imagetovideo.utils.extension

import android.view.View

var View.isHidden : Boolean
    get(){
        return visibility == View.GONE
    }
    set(value) {
        if (value) {
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
            }
        } else {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
            }
        }
    }