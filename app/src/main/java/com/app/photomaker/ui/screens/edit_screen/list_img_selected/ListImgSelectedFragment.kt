package com.app.photomaker.ui.screens.edit_screen.list_img_selected

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.app.photomaker.R
import com.app.photomaker.base.BaseFragment
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.databinding.LayoutListImageSelectedBinding
import com.app.photomaker.eventbus.HandleImageEvent
import com.app.photomaker.ext.CoroutineExt
import com.app.photomaker.model.ImageSelected
import com.app.photomaker.ui.adapters.ImageSelectorHorizontalAdapter
import com.app.photomaker.utils.MarginItemDecoration
import com.app.photomaker.ui.screens.edit_screen.EditorVM
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ListImgSelectedFragment: BaseFragment<LayoutListImageSelectedBinding>() {
    private val viewModel: EditorVM by activityViewModels()
    private val myListImageSelected = ArrayList<ImageSelected?>()
    private var oldPositionImageSelected = 0
    private var imageSelectorHorizontalAdapter: ImageSelectorHorizontalAdapter?= null
    private var viewpagerHandleImage: ViewPager2 ?= null
    private var onAddImageListener : ((Boolean) -> Unit) ?= null
    companion object {
        fun newInstance(): ListImgSelectedFragment {
            return ListImgSelectedFragment()
        }
    }

    fun setViewpagerHandleImage(viewpagerHandleImage: ViewPager2) {
        this.viewpagerHandleImage = viewpagerHandleImage
    }

    fun setOnAddImageListener(onAddImageListener: (Boolean) -> Unit){
        this.onAddImageListener = onAddImageListener
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_list_image_selected
    }

    override fun initView() {
        imageSelectorHorizontalAdapter = ImageSelectorHorizontalAdapter(
            myListImageSelected,
            onClickImageSelected = { position, _ ->
                updateItemSelectedInList(position)
            },
            onClickAddImage = {
                onAddImageListener?.invoke(true)
            }
        )
        binding.rvListImageSelected.layoutManager = GridLayoutManager(requireContext(),5, GridLayoutManager.VERTICAL, false)
        binding.rvListImageSelected.addItemDecoration(
            MarginItemDecoration(
                marginLeft = resources.getDimensionPixelOffset(R.dimen.dp10)
            )
        )
        binding.rvListImageSelected.adapter = imageSelectorHorizontalAdapter
    }

    private fun updateItemSelectedInList(position: Int) {
        myListImageSelected[oldPositionImageSelected]?.isSelected = false
        imageSelectorHorizontalAdapter?.notifyItemChanged(oldPositionImageSelected)
        myListImageSelected[position]?.isSelected = true
        imageSelectorHorizontalAdapter?.notifyItemChanged(position)
        oldPositionImageSelected = position
        viewpagerHandleImage?.setCurrentItem(position, false)
    }

    override fun initListener() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observerLiveData() {
        viewModel.apply {
            myCurrentImageSelected.observe(this@ListImgSelectedFragment) {
                if (it.isNotEmpty()) {
                    oldPositionImageSelected = 0
                    myListImageSelected.clear()
                    myListImageSelected.addAll(it)
                    if(myListImageSelected.size < 5)
                        myListImageSelected.add(ImageSelected())
                    imageSelectorHorizontalAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

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
            HandleImageEvent.REMOVE_IMAGE_SELECTED_FROM_HANDLE_IMAGE_SCREEN -> {
                if(oldPositionImageSelected == event.position){
                    myListImageSelected.removeAt(oldPositionImageSelected)
                    imageSelectorHorizontalAdapter?.notifyItemRemoved(oldPositionImageSelected)
                    imageSelectorHorizontalAdapter?.notifyItemRangeChanged(oldPositionImageSelected,myListImageSelected.size)
                    if(myListImageSelected.last()?.uriInput != null && myListImageSelected.size < 5){
                        myListImageSelected.add(ImageSelected())
                        imageSelectorHorizontalAdapter?.notifyItemInserted(myListImageSelected.size)
                    }
                    if(myListImageSelected.size == 1){
                        CoroutineExt.runOnMainAfterDelay(500) {
                            onAddImageListener?.invoke(false)
                        }
                    }else{
                        var pos = event.position
                        if (pos != null) {
                            if(pos != 0) pos -= 1
                            updateItemSelectedInList(pos)
                        }
                    }
                }
            }
            HandleImageEvent.UPDATE_IMAGE_FILTER_BRIGHTNESS_IN_LIST_IMAGE_SELECTED -> {
                val positionImageChange = viewpagerHandleImage?.currentItem
                if (positionImageChange != null) {
                    myListImageSelected[positionImageChange]?.bitmap = event.imageSelected?.bitmap
                    imageSelectorHorizontalAdapter?.notifyItemChanged(positionImageChange)
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}