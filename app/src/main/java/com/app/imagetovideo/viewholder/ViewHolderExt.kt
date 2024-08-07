package com.app.imagetovideo.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.ViewHolder.setupUIItemImageFromGallery(view: View){
    when(view.layoutParams){
        is GridLayoutManager.LayoutParams ->{
            val layoutParam = view.layoutParams as GridLayoutManager.LayoutParams
            view.post {
                layoutParam.height = view.width
                view.requestLayout()
            }
        }
    }
}
val ratioItem = 110/50f

fun RecyclerView.ViewHolder.setupUIItemImageSelected(view: View){
    when(view.layoutParams){
        is GridLayoutManager.LayoutParams ->{
            val layoutParam = view.layoutParams as GridLayoutManager.LayoutParams
            view.post {
                layoutParam.height = (layoutParam.width * ratioItem).toInt()
                view.requestLayout()
                Log.d("INIT_VIEW_HOLDER", "${layoutParam.width }  - ${layoutParam.height}")
            }
        }
    }
}


