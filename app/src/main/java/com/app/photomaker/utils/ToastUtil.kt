package com.app.photomaker.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {
    var toastAction: ((String) -> Unit)? = null

    fun showToast(text: String, context: Context?) {
        toastAction?.invoke(text) ?: Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
