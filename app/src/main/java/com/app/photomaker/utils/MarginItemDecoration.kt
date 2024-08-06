package com.app.photomaker.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private  var marginLeft: Int =0, private  var marginTop: Int =0, private  var marginRight: Int = 0, private  var marginBottom: Int = 0) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect){
            top = marginTop
            left = marginLeft
            right = marginRight
            bottom = marginBottom
        }
    }
}

class MarginItemDecorationListImageSelectedFragment(private  var marginLeft: Int? =0, private  var marginTop: Int? =0, private  var marginRight: Int? = 0, private  var marginBottom: Int?= 0) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect){
            top = marginTop!!
            right = marginRight!!
            if(parent.getChildAdapterPosition(view) != 0)
                left = marginLeft!!
            bottom = marginBottom!!
        }
    }
}

class MarginItemDecorationRecycleViewImagePicker(private  var marginLeft: Int =0, private  var marginTop: Int =0, private  var marginRight: Int = 0, private  var marginBottom: Int = 0, private var marginBottomLastItem: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect){
            if(parent.adapter?.itemCount != null){
                val itemsAtLastRow = if (parent.adapter?.itemCount?.rem(3) == 0) 3 else parent.adapter?.itemCount?.rem(3)
                left = marginLeft
                right = marginRight
                top = marginTop
                bottom = if(parent.getChildAdapterPosition(view) > (parent.adapter?.itemCount!!.minus(itemsAtLastRow!! + 1))
                ) marginBottomLastItem
                else marginBottom
            }

        }
    }
}

class MarginItemDecorationRecycleViewImageSelected(private  var marginLeft: Int? =0, private  var marginTop: Int? =0, private  var marginRight: Int? = 0, private  var marginBottom: Int?= 0) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect){
            if(parent.getChildAdapterPosition(view) != 0){
                left = marginLeft!!
            }
            right = marginRight!!
            top = marginTop!!
            bottom = marginBottom!!
        }
    }
}
