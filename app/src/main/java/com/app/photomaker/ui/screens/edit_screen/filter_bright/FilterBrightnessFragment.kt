package com.app.photomaker.ui.screens.edit_screen.filter_bright

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.photomaker.R
import com.app.photomaker.base.BaseFragment
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.data.model.FilterBrightness
import com.app.photomaker.databinding.LayoutListFilterBrightnessBinding
import com.app.photomaker.eventbus.HandleImageEvent
import com.app.photomaker.model.ImageSelected
import com.app.photomaker.ui.adapters.FilterBrightnessAdapter
import com.app.photomaker.utils.MarginItemDecoration
import com.app.photomaker.utils.PhotoUtils
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class FilterBrightnessFragment: BaseFragment<LayoutListFilterBrightnessBinding>() {
    private val filterBrightnessVM: FilterBrightnessVM by viewModels()
    private val listFilter = ArrayList<FilterBrightness?>()
    private var oldPositionPreview = 2
    private var filterAdapter: FilterBrightnessAdapter?= null
    private var imageSelectedFilter: ImageSelected?= null
    companion object {
        fun newInstance(): FilterBrightnessFragment {
            return FilterBrightnessFragment()
        }
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_list_filter_brightness
    }

    override fun initView() {
        filterAdapter = FilterBrightnessAdapter(
            listFilter,
            onClickItemFilter = { position, filterBrightness ->
                listFilter[oldPositionPreview]?.preview = false
                filterAdapter?.notifyItemChanged(oldPositionPreview)
                listFilter[position]?.preview = true
                filterAdapter?.notifyItemChanged(position)
                oldPositionPreview = position
                EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.UPDATE_FILTER_BRIGHTNESS_EVENT, filterBrightness))
            },
            onClickSaveFilter = { _, filterBrightness ->
                PhotoUtils.saveImageToCache(filterBrightness?.bitmapImageOrigin, requireContext(),
                    imageSelectedFilter, onSuccess = {
                        imageSelectedFilter?.uriResultCutImageInCache = it.toString()
                        imageSelectedFilter?.uriResultFilterImageInCache = it.toString()
                        Log.d("SAVE_FILTERED_IMAGE", "$it")
                    }, onFailure = { message ->
                        Log.d("SAVE_FILTERED_IMAGE", message ?:"")
                    })
                imageSelectedFilter?.bitmap = filterBrightness?.bitmapImageOrigin
                EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.SAVE_FILTER_BRIGHTNESS_EVENT, filterBrightness, imageSelectedFilter))
            },
            activity = requireActivity()
        )
        binding.rvListFilterBrightness.layoutManager = GridLayoutManager(requireContext(),5, GridLayoutManager.VERTICAL, false)
        binding.rvListFilterBrightness.addItemDecoration(
            MarginItemDecoration(
                marginLeft = resources.getDimensionPixelOffset(R.dimen.dp10)
            )
        )
        binding.rvListFilterBrightness.adapter = filterAdapter
    }

    override fun initListener() {}


    override fun observerLiveData() {
        filterBrightnessVM.apply {
            myFilterBrightness.observe(this@FilterBrightnessFragment) { filterBrightness ->
                listFilter[filterBrightness.indexCreated] = filterBrightness
                filterAdapter?.notifyItemChanged(filterBrightness.indexCreated)
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun generateDefaultFilter() {
        oldPositionPreview = 2
        listFilter.clear()
        for (index in 0..4) {
            val filterBrightness = FilterBrightness()
            filterBrightness.indexCreated = index
            listFilter.add(filterBrightness)
        }
        filterAdapter?.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("RtlHardcoded")
    @Subscribe
    fun onMessageEvent(event: HandleImageEvent) {
        when (event.message) {
            HandleImageEvent.UPDATE_IMAGE_LIST_EVENT -> {
                if (event.isGotoFilterMode == true) {
                    imageSelectedFilter = event.imageSelected
                    generateDefaultFilter()
                    filterBrightnessVM.generateFilter(imageSelectedFilter)
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}