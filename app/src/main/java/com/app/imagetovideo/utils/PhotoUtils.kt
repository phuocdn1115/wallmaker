package com.app.imagetovideo.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.app.imagetovideo.model.ImageSelected
import com.yalantis.ucrop.util.BitmapLoadUtils
import java.io.*


class PhotoUtils {
    companion object {
        fun changeBitmapContrastBrightness(
            bmp: Bitmap,
            contrast: Float,
            brightness: Float
        ): Bitmap {
            val cm = ColorMatrix(
                floatArrayOf(contrast, 0f, 0f, 0f, brightness, 0f, contrast, 0f, 0f, brightness, 0f, 0f, contrast, 0f, brightness, 0f, 0f, 0f, 1f, 0f)
            )
            val ret = Bitmap.createBitmap(
                bmp.width, bmp.height,
                bmp.config
            )
            val canvas = Canvas(ret)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(cm)
            canvas.drawBitmap(bmp, 0f, 0f, paint)
            return ret
        }
        @Throws(FileNotFoundException::class)
        fun saveImageToCache(croppedBitmap: Bitmap?, context: Context, imageSelected: ImageSelected?, onSuccess: (Uri?)->Unit, onFailure: (String?)-> Unit) {
            val compressQuality = 90
            var outputStream: OutputStream? = null
            var outStream: ByteArrayOutputStream? = null
            var mOutputPath: String?
            try {
                mOutputPath = imageSelected?.getChildNameCacheFile()
                outputStream = FileOutputStream(File(context.cacheDir, mOutputPath), false)
                outStream = ByteArrayOutputStream()
                croppedBitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, outStream)
                outputStream.write(outStream.toByteArray())
//                croppedBitmap?.recycle()
                val uriResult = Uri.fromFile(File(context.cacheDir, mOutputPath))
                onSuccess.invoke(uriResult)
            } catch (exc: Exception) {
                onFailure.invoke(exc.message)
            } finally {
                BitmapLoadUtils.close(outputStream)
                BitmapLoadUtils.close(outStream)
            }
        }
    }
}