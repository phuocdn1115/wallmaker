package com.app.photomaker.utils

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.photomaker.R
import com.google.android.material.snackbar.Snackbar

object SnackBarUtils {
    fun showSnackBarNoInternet(view: View, message: String, imageResource: Int, duration: Int, layoutInflater: LayoutInflater, marginBottom: Float = 0f) {
        val snackBar = Snackbar.make(view, "", duration)
        val customSnackView: View =
            layoutInflater.inflate(R.layout.view_snackbar_simple, null)
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        val messageText: TextView = customSnackView.findViewById(R.id.tv_message)
        val icon: ImageView = customSnackView.findViewById(R.id.im_action_left)
        messageText.text = message
        icon.setImageResource(imageResource)
        snackBar.view.setBackgroundResource(R.drawable.bg_snackbar)
        snackBarLayout.setPadding(0, 0, 0, 0)
        snackBar.view.translationY = -(marginBottom)
        snackBarLayout.addView(customSnackView, 0)
        snackBar.show()
    }
}