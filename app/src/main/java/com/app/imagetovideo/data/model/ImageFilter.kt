package com.app.imagetovideo.data.model

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

data class ImageFilter(
    val name: String? = "",
    val filter: GPUImageFilter = GPUImageFilter(),
    val filterPreview: Bitmap? = null,
    val filterSave: Bitmap? = null,
    var isSelected: Boolean = false
)
