package com.app.imagetovideo.ui.screens.edit_screen.filter_image

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.view.marginLeft
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.ImageFilter
import com.app.imagetovideo.databinding.LayoutListFilterBrightnessBinding
import com.app.imagetovideo.eventbus.HandleImageEvent
import com.app.imagetovideo.model.ImageSelected
import com.app.imagetovideo.ui.adapters.FilterImageAdapter
import com.app.imagetovideo.utils.MarginItemDecoration
import com.app.imagetovideo.utils.PhotoUtils
import com.app.imagetovideo.utils.extension.widthScreen
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class FilterImageFragment : BaseFragment<LayoutListFilterBrightnessBinding>() {
    private val viewModel: FilterImageVM by viewModels()

    private val listImageFilter = arrayListOf<ImageFilter?>()
    private var filterImageAdapter: FilterImageAdapter? = null
    private var imageFilterSelected: ImageFilter? = null
    private var imageSelected: ImageSelected? = null
    private var oldPositionSelected: Int = 0

    val onSaveFilterImage = object :() -> Unit {
        override fun invoke() {
            Log.d("SAVE_FILTERED_IMAGE", "-------------------------------------------------")
            PhotoUtils.saveImageToCache(listImageFilter[oldPositionSelected]?.filterPreview, requireContext(),
                imageSelected, onSuccess = {
                    imageSelected?.uriResultCutImageInCache = it.toString()
                    imageSelected?.uriResultFilterImageInCache = it.toString()
                    Log.d("SAVE_FILTERED_IMAGE", "$it")
                }, onFailure = { message ->
                    Log.d("SAVE_FILTERED_IMAGE", message ?:"")
                })
           imageSelected?.bitmap = listImageFilter[oldPositionSelected]?.filterPreview
            EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.SAVE_FILTER_BRIGHTNESS_EVENT, imageFilterSelected, imageSelected))
        }

    }


    companion object {
        fun newInstance(): FilterImageFragment {
            return FilterImageFragment()
        }
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_list_filter_brightness
    }

    override fun initView() {
        initAdapter()
    }

    private fun initAdapter() {
        filterImageAdapter = FilterImageAdapter(listImageFilter) { data, position ->
            // Handle filter image clicked
            if (position == oldPositionSelected) return@FilterImageAdapter
            listImageFilter[oldPositionSelected]?.isSelected = false
            filterImageAdapter?.notifyItemChanged(oldPositionSelected)
            oldPositionSelected = position
            listImageFilter[position]?.isSelected = true
            filterImageAdapter?.notifyItemChanged(position)
            EventBus.getDefault()
                .post(HandleImageEvent(HandleImageEvent.UPDATE_FILTER_BRIGHTNESS_EVENT, data))
            binding.rvListFilterBrightness.scrollToPosition(position)
        }
        binding.rvListFilterBrightness.setPadding(
            (widthScreen / 2) - resources.getDimensionPixelOffset(R.dimen.dp33),
            resources.getDimensionPixelOffset(R.dimen.dp0),
            (widthScreen / 2) - resources.getDimensionPixelOffset(R.dimen.dp33),
            resources.getDimensionPixelOffset(R.dimen.dp0)
        )

        binding.rvListFilterBrightness.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(MarginItemDecoration(marginLeft = resources.getDimensionPixelOffset(R.dimen.dp5)))
        }
        binding.rvListFilterBrightness.adapter = filterImageAdapter

    }

    override fun initListener() {

    }

    private fun refreshUI() {
        oldPositionSelected = 0
        listImageFilter.clear()
        adapterNotifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun adapterNotifyDataSetChanged() {
        filterImageAdapter?.notifyDataSetChanged()

    }

    override fun observerLiveData() {
        viewModel.apply {
            filterImageLiveData.observe(this@FilterImageFragment) { result ->
                when (result) {
                    is Result.InProgress -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 7 - ${result.data.size}")
                        listImageFilter.apply {
                            clear()
                            addAll(result.data)
                        }
                        EventBus.getDefault().post(
                            HandleImageEvent(
                                HandleImageEvent.UPDATE_FILTER_BRIGHTNESS_EVENT,
                                listImageFilter.first()
                            )
                        )
                        adapterNotifyDataSetChanged()
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? {
        return null
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
                Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 3 ")

                if (event.isGotoFilterMode == true) {
                    Log.i("GO_TO_FILTER_MODE", "onMessageEvent: 4 ")

                    imageSelected = event.imageSelected
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        context?.contentResolver,
                        Uri.parse(imageSelected?.uriResultCutImageInCache)
                    )
                    EventBus.getDefault()
                        .post(HandleImageEvent(HandleImageEvent.UPDATE_FILTER_BRIGHTNESS_EVENT, ImageFilter(
                            filterPreview = bitmap
                        )))
                    refreshUI()
                    viewModel.loadImageFilter(bitmap)
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

}