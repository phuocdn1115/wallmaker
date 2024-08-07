package com.app.imagetovideo.data.model

import android.graphics.Bitmap

class FilterBrightness {
    var bitmapImageCropped: Bitmap? = null
    var bitmapImageOrigin: Bitmap?= null
    var preview: Boolean = false
    var isOrigin: Boolean = false
    var isLoading: Boolean = true
    var indexCreated: Int = 0
}