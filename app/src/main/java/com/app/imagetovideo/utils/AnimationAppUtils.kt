package com.app.imagetovideo.utils

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.app.imagetovideo.ext.CoroutineExt
import com.thekhaeng.pushdownanim.PushDownAnim

object AnimationAppUtils {
    fun animateFollowY(view: View, duration: Long, valueY: Float) {
        view.animate()
            .translationY(valueY)
            .duration = duration
    }

    fun animateFollowX(view: View, duration: Long, valueX: Float) {
        view.animate()
            .translationX(valueX)
            .duration = duration
    }

    fun animationSetHeight(
        heightStart: Int,
        heightEnd: Int,
        duration: Long,
        viewSet: View,
        callBackEndAnimation: () -> Unit
    ) {
        val anim = ValueAnimator.ofInt(heightStart, heightEnd)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = viewSet.layoutParams
            layoutParams.height = value
            viewSet.layoutParams = layoutParams
            viewSet.requestLayout()

            /** End animation*/
            if (value == heightEnd) {
                callBackEndAnimation.invoke()
            }
        }
        anim.duration = duration
        anim.start()
    }

    fun animationSetWidth(
        widthStart: Int,
        widthEnd: Int,
        duration: Long,
        viewSet: View,
        callBackStartAnimation: () -> Unit,
        callBackEndAnimation: () -> Unit
    ) {
        val anim = ValueAnimator.ofInt(widthStart, widthEnd)
        anim.apply {
            this.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams = viewSet.layoutParams
                if (value != 0) layoutParams.width = value
                else layoutParams.width = 1
                viewSet.layoutParams = layoutParams
                viewSet.requestLayout()
                Log.d("CHECK_WIDTH", value.toString())
            }
            this.addListener(object : Animator.AnimatorListener {

                override fun onAnimationStart(p0: Animator) {
                    callBackStartAnimation.invoke()
                }

                override fun onAnimationEnd(p0: Animator) {
                    callBackEndAnimation.invoke()
                }

                override fun onAnimationCancel(p0: Animator) {
                }

                override fun onAnimationRepeat(p0: Animator) {
                }

            })
            this.duration = duration
            this.start()
        }
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

}