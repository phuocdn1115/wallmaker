package com.app.photomaker.ui.skeleton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.app.photomaker.R
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.databinding.LayoutSkeletonListVideoBinding
import com.facebook.shimmer.ShimmerFrameLayout

class HomeScreenSkeleton : RelativeLayout, BaseLoadingView {
    private lateinit var mBinding: LayoutSkeletonListVideoBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView(context)
    }

    fun initView(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_skeleton_list_video, this, true)
    }


    override fun getLayoutParent(): View {
        return mBinding.container
    }

    override fun getLayoutShimmer(): ShimmerFrameLayout {
        return mBinding.layoutShimmer
    }
}
