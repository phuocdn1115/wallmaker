package com.app.photomaker.base.handler

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.app.photomaker.R
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.utils.CommonUtils
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File
import com.app.photomaker.di.GlideApp

object GlideHandler {
    fun setImageFormUrl(imageView: ImageView, url: String?, colorIfFailure: Int = CommonUtils.randomColor()) {
        val context = imageView.context
        GlideApp.with(context)
            .load(url)
            .transition(GenericTransitionOptions.with(R.anim.fade_in))
            .error(ColorDrawable(colorIfFailure))
            .into(imageView)
    }

    fun setImageFormDrawableResource(imageView: ImageView, drawable: Int?, colorIfFailure: Int = CommonUtils.randomColor()) {
        val context = imageView.context
        GlideApp.with(context)
            .load(drawable)
            .transition(GenericTransitionOptions.with(R.anim.fade_in))
            .error(ColorDrawable(colorIfFailure))
            .into(imageView)
    }

    @SuppressLint("CheckResult")
    fun setImageFormUrlWithCallBack(imageView: ImageView, url: String?, onFinish: () -> Unit) {
        val context = imageView.context
        GlideApp.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFinish.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onFinish.invoke()
                    return false
                }

            })
            .into(imageView)
    }


    fun downLoadFileFromUrlImage(url: String, options: RequestOptions? = null, onSuccess: (file: File) -> Unit, onError: (errorMassage: String?) -> Unit) {
        var requestOptions = options ?: RequestOptions().override(Target.SIZE_ORIGINAL)
        requestOptions = requestOptions.priority(Priority.HIGH)
        GlideApp.with(WallpaperMakerApp.instance)
            .downloadOnly()
            .load(url)
            .apply(requestOptions)
            .listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<File>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onError.invoke(e?.message)
                    return false
                }
                override fun onResourceReady(
                    resource: File?,
                    model: Any?,
                    target: Target<File>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) onSuccess.invoke(resource)
                    else onError.invoke("File null")
                    return false
                }
            })
            .submit()
    }
}