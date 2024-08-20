package com.app.imagetovideo.ui.screens.edit_screen.filter_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.app.imagetovideo.data.model.ImageFilter
import com.app.imagetovideo.model.ImageSelected
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import java.io.File
import javax.inject.Inject

class FilterImageRepository @Inject constructor(@ApplicationContext val context: Context) {
    fun getImageFilter(image: ImageSelected?): List<ImageFilter> {

        val bitmapImageCroppedWithFilter =
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                Uri.parse(image?.uriResultCutImageInCache)
            )
        val uriImageFilterSaved =
            if (image?.uriResultFilterImageInCache != null) Uri.parse(image.uriResultFilterImageInCache)
            else Uri.fromFile(File(image?.uriInput ?: ""))
        val bitmapImageOriginWithFilter = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            uriImageFilterSaved
        )
        val imageFilters: ArrayList<ImageFilter> = ArrayList()
        processFilterImage(bitmapImageCroppedWithFilter, bitmapImageOriginWithFilter, imageFilters)
        return imageFilters
    }

    private fun processFilterImage(
        bmImageCropped: Bitmap,
        bmImageOriginFilter: Bitmap,
        result: MutableList<ImageFilter>
    ) {
        Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 5.2 ")

        /**
         * Color Matrix Filters
         */
        val gpuImageCropped = GPUImage(context).apply {
            setImage(bmImageCropped)
        }

        val gpuImageOriginFilter = GPUImage(context).apply {
            setImage(bmImageOriginFilter)
        }

        GPUImageFilter().also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Normal",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied,
                    isSelected = true
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.2f, 0.0f,
                0.1f, 0.1f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Retro",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            0.9f,
            floatArrayOf(
                0.4f, 0.6f, 0.5f, 0.0f,
                0.0f, 0.4f, 1.0f, 0.0f,
                0.05f, 0.1f, 0.4f, 0.4f,
                1.0f, 1.0f, 1.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Just",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.25f, 0.0f, 0.2f, 0.0f,
                0.0f, 1.0f, 0.2f, 0.0f,
                0.0f, 0.3f, 1.0f, 0.3f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Hume",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.6f, 0.4f, 0.2f, 0.05f,
                0.0f, 0.8f, 0.3f, 0.05f,
                0.3f, 0.3f, 0.5f, 0.08f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Desert",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.0f, 0.05f, 0.0f, 0.0f,
                -0.2f, 1.1f, -0.2f, 0.11f,
                0.2f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "OldTime",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.0f, 0.0f, 0.08f, 0.0f,
                0.4f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.1f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Limo",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }
        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.5f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Solar",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0f, 1f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0.6f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Neutron",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.64f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Milk",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        /**
         * Black Filter
         */

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Clue",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        /**
         * Black Filter
         */
        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Muli",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0f, 0f, 1f, 0f,
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Aero",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.763f, 0.0f, 0.2062f, 0f,
                0.0f, 0.9416f, 0.0f, 0f,
                0.1623f, 0.2614f, 0.8052f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Classic",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.5162f, 0.3799f, 0.3247f, 0f,
                0.039f, 1.0f, 0f, 0f,
                -0.4773f, 0.461f, 1.0f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Atom",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }

        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.0f, 0.0f, 0.5183f, 0.3183f,
                0.0f, 0.5497f, 0.5416f, 0f,
                0.5237f, 0.5269f, 0.0f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Mars",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )
        }
        GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.0f, -0.3831f, 0.3883f, 0.0f,
                0.0f, 1.0f, 0.2f, 0f,
                -0.1961f, 0.0f, 1.0f, 0f,
                0f, 0f, 0f, 1f
            )
        ).also { filter ->
            gpuImageCropped.setFilter(filter)
            gpuImageOriginFilter.setFilter(filter)
            result.add(
                ImageFilter(
                    name = "Yeli",
                    filter = filter,
                    filterPreview = gpuImageCropped.bitmapWithFilterApplied,
                    filterSave = gpuImageOriginFilter.bitmapWithFilterApplied
                )
            )

        }
    }
}
