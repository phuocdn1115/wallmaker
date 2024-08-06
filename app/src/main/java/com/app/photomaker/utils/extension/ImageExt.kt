package com.app.photomaker.utils.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.util.DisplayMetrics
import com.app.photomaker.enums.CropType

var displayMetrics: DisplayMetrics ?= null
val widthScreen: Int get() = displayMetrics?.widthPixels ?: 1
val heightScreen: Int get() = displayMetrics?.heightPixels ?: 2

fun Bitmap.crop(cropType: CropType = CropType.CENTER, w: Int = widthScreen, h: Int = heightScreen): Bitmap? {
    val width = if (w <= 0) this.width else w
    val height = if (h <= 0) this.height else h
    val config = if (config != null) config else Bitmap.Config.ARGB_8888
    val bitmap = Bitmap.createBitmap(width, height, config)
    bitmap.setHasAlpha(true)
    val scaleX = width.toFloat() / this.width
    val scaleY = height.toFloat() / this.height
    val scale = kotlin.math.max(scaleX, scaleY)
    val scaledWidth = scale * this.width
    val scaledHeight = scale * this.height
    val left: Float = (width - scaledWidth) / 2
    val top: Float = getTop(cropType, scaledHeight, height)
    val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
    val canvas = Canvas(bitmap)
    canvas.drawBitmap(this, null, targetRect, null)
    return bitmap
}

private fun getTop(cropType: CropType, scaledHeight: Float, h: Int): Float {
    return when (cropType) {
        CropType.TOP -> 0f
        CropType.CENTER -> (h - scaledHeight) / 2f
        CropType.BOTTOM -> h - scaledHeight
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

fun decodeBitmap(filePath: String?, reqWidth: Int = widthScreen, reqHeight: Int = heightScreen): Bitmap? {
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, this)

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        inJustDecodeBounds = false

        BitmapFactory.decodeFile(filePath, this)
    }
}