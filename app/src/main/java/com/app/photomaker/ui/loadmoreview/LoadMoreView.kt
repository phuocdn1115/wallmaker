package com.app.photomaker.ui.loadmoreview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.app.photomaker.R
import com.app.photomaker.databinding.LayoutFooterLoadMoreBinding
import com.bumptech.glide.Glide
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent

class LoadMoreView : SimpleComponent, RefreshFooter {

    private val binding: LayoutFooterLoadMoreBinding

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val inflater = context.getSystemService((Context.LAYOUT_INFLATER_SERVICE)) as LayoutInflater
        binding = LayoutFooterLoadMoreBinding.inflate(inflater, this, true)
        Glide.with(context).load(R.raw.image_loading).into(binding.imgFooterLoadMore)
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean) : Int {

       binding.imgFooterLoadMore.visibility = INVISIBLE
        if (success) {
            super.onFinish(refreshLayout, success)
            return 0
        }
        return if(!success){
            super.onFinish(refreshLayout, success)
            0
        } else{
            binding.imgFooterLoadMore.visibility = View.GONE
            super.onFinish(refreshLayout, success)
            0
        }
    }

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        return super.setNoMoreData(noMoreData)
    }


    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        binding.imgFooterLoadMore.visibility = View.VISIBLE
        when (newState) {
            RefreshState.PullUpToLoad -> {
            }
            RefreshState.LoadFinish -> {

            }
            RefreshState.LoadReleased -> {
            }
            RefreshState.Loading -> {
            }
            else -> {}
        }


    }

}