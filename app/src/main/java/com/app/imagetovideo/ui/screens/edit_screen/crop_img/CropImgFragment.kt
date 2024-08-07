package com.app.imagetovideo.ui.screens.edit_screen.crop_img

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.RelativeLayout
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.databinding.LayoutCropImageBinding
import com.app.imagetovideo.eventbus.HandleImageEvent
import com.app.imagetovideo.ext.CoroutineExt
import com.app.imagetovideo.model.ImageSelected
import com.app.imagetovideo.utils.extension.displayMetrics
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.OverlayView
import com.yalantis.ucrop.view.TransformImageView
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class CropImgFragment: BaseFragment<LayoutCropImageBinding>() {
    private var myGestureCropImageView: GestureCropImageView?= null
    private var myOverlayView: OverlayView?= null
    private lateinit var myBlockingView: RelativeLayout
    private var imageSelected: ImageSelected?= null
    private val myTransformImageListener = object : TransformImageView.TransformImageListener {
        override fun onLoadComplete() {
            binding.uCropView.animate().alpha(1F).setDuration(300).interpolator = AccelerateInterpolator()
            binding.container.animate().alpha(1F).setDuration(500).interpolator = AccelerateInterpolator()
            myBlockingView.isClickable = false
        }

        override fun onLoadFailure(e: Exception) {

        }

        override fun onRotate(currentAngle: Float) {

        }

        override fun onScale(currentScale: Float) {

        }

    }
    companion object {
        fun newInstance(bundle: Bundle): CropImgFragment {
            val fragment = CropImgFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_crop_image
    }

    override fun initView() {
        myGestureCropImageView = binding.uCropView.cropImageView
        myGestureCropImageView?.setTransformImageListener(myTransformImageListener)
        myOverlayView = binding.uCropView.overlayView
        myGestureCropImageView?.setPadding(0, resources.getDimensionPixelSize(R.dimen.dp100), 0, resources.getDimensionPixelSize(R.dimen.dp100))
        myOverlayView?.setPadding(0, resources.getDimensionPixelSize(R.dimen.dp100), 0, resources.getDimensionPixelSize(R.dimen.dp100))
        doOptionsCropImage()
        addBlockingView()
        initData()
    }

    private fun initData() {
        imageSelected = arguments?.get(ImageSelected.KeyData.IMAGE_SELECTED) as ImageSelected
        try {
            myGestureCropImageView?.setImageUri(
                Uri.fromFile(File(imageSelected?.uriInput ?: "")),
                Uri.fromFile(File(requireContext().cacheDir, imageSelected?.getChildNameCacheFile() ?: ""))
            )
        } catch (e: Exception) {}
    }

    override fun initListener() {

    }

    override fun observerLiveData() {

    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun doOptionsCropImage() {
        /** Crop image view option */
        myGestureCropImageView?.apply {
            maxBitmapSize = CropImageView.DEFAULT_MAX_BITMAP_SIZE
            setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER)
            setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION.toLong())
            targetAspectRatio = displayMetrics!!.widthPixels.toFloat() / displayMetrics!!.heightPixels
            setImageToWrapCropBounds()
            isRotateEnabled = false
        }

        /** Overlay view option */
        myOverlayView?.apply {
            freestyleCropMode = OverlayView.FREESTYLE_CROP_MODE_DISABLE
            setDimmedColor(resources.getColor(R.color.color_BF000000, null))
            setCircleDimmedLayer(false)
            setShowCropFrame(true)
            setCropFrameColor(resources.getColor(R.color.ucrop_color_default_crop_frame, null))
            setCropFrameStrokeWidth(resources.getDimensionPixelSize(R.dimen.dp2))
        }
    }

    fun cropAndSaveImage(isGotoFilterMode: Boolean ?= true, position: Int?= null) {
            val compressQuality = 90
            val compressFormat = Bitmap.CompressFormat.JPEG
            myGestureCropImageView?.cropAndSaveImage(compressFormat, compressQuality, object : BitmapCropCallback {
                override fun onBitmapCropped(
                    resultUri: Uri,
                    offsetX: Int,
                    offsetY: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    CoroutineExt.runOnMain {
                        imageSelected?.uriResultCutImageInCache = resultUri.toString()
                        myGestureCropImageView?.setImageUri(
                            Uri.fromFile(File(imageSelected?.uriInput ?: "")),
                            Uri.fromFile(File(requireContext().cacheDir, imageSelected?.getChildNameCacheFile() ?: "")))
                        EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.UPDATE_IMAGE_LIST_EVENT, imageSelected, isGotoFilterMode, position))
                    }
                }

                override fun onCropFailure(t: Throwable) {
                    Log.d("PREVIEW_VIDEO_FRAGMENT", "FAILURE----------------------------------${t.message}-------------------------------------")
                    CoroutineExt.runOnMain {
                        EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.RETRY_CROP_IMAGE_TO_PREVIEW_SCREEN))
                    }
                }
            })
    }

    private fun addBlockingView() {
        myBlockingView = RelativeLayout(requireContext())
        val layoutParam: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        myBlockingView.layoutParams = layoutParam
        myBlockingView.isClickable = true
        binding.container.addView(myBlockingView)
    }

    /** Khi vào onResume() (Tức là fragment này đang hiển thị) thì mới register các sự kiện xoay và cắt ảnh trên fragment này
     *  Khi vào onPause() (Tức là khi fragment này không hiển thị nữa) thì unregister các sự kiện xoay và cắt ảnh trên fragment này */
    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }
    
    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
    @SuppressLint("RtlHardcoded")
    @Subscribe
    fun onMessageEvent(event: HandleImageEvent) {
        when (event.message) {
            HandleImageEvent.ROTATE_IMAGE_EVENT -> {
                myGestureCropImageView?.postRotate(90F)
                myGestureCropImageView?.setImageToWrapCropBounds()
            }
            HandleImageEvent.CROP_SAVE_IMAGE_EVENT -> {
                CoroutineExt.runOnIO {
                    cropAndSaveImage()
                }

            }
            HandleImageEvent.UPDATE_IMAGE_FILTER_BRIGHTNESS_IN_LIST_IMAGE_SELECTED ->{
                imageSelected = event.imageSelected
                try {
                    myGestureCropImageView?.setImageUri(
                        Uri.parse(imageSelected?.uriResultCutImageInCache ?: ""),
                        Uri.fromFile(File(requireContext().cacheDir, imageSelected?.getChildNameCacheFile() ?: ""))
                    )
                } catch (e: Exception) {}
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}