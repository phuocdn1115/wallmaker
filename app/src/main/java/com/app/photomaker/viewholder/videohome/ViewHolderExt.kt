package com.app.photomaker.viewholder.videohome

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.photomaker.R

fun RecyclerView.ViewHolder.setupUIItemImgVideoHome(view: View) {
    when(view.layoutParams){
        is StaggeredGridLayoutManager.LayoutParams ->{
            val layoutParam = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
            view.post {
                layoutParam.height = view.width * 343 / 156
                view.requestLayout()
            }
        }
        else -> {
            val layoutParams = view.layoutParams
            view.post {
                layoutParams.width = view.context.resources.getDimensionPixelOffset(R.dimen.dp156)
                layoutParams.height = view.context.resources.getDimensionPixelOffset(R.dimen.dp343)
                view.requestLayout()
            }
        }
    }
}