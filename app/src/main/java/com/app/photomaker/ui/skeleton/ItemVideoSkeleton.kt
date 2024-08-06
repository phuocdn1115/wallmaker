package com.app.photomaker.ui.skeleton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.app.photomaker.R
import com.app.photomaker.databinding.LayoutSkeletonItemVideoBinding

class ItemVideoSkeleton : ConstraintLayout {
    private var binding: LayoutSkeletonItemVideoBinding
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        this.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(R.dimen.dp343))
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_skeleton_item_video, this, true)
    }
}