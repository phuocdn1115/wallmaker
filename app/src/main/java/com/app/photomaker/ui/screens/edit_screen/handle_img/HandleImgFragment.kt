package com.app.photomaker.ui.screens.edit_screen.handle_img

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.app.photomaker.ads.banner.BannerAdsManager
import com.app.photomaker.base.BaseFragment
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.data.model.Template
import com.app.photomaker.enums.DescriptionControlTabType
import com.app.photomaker.enums.EditorTabType
import com.app.photomaker.enums.HandleImageMode
import com.app.photomaker.eventbus.HandleImageEvent
import com.app.photomaker.model.ImageSelected
import com.app.photomaker.ui.screens.edit_screen.crop_img.CropImgFragment
import com.app.photomaker.ui.screens.edit_screen.filter_bright.FilterBrightnessFragment
import com.app.photomaker.utils.StatusBarUtils
import com.app.photomaker.utils.extension.heightScreen
import com.app.photomaker.utils.extension.widthScreen
import com.app.photomaker.utils.setSafeOnClickListener
import com.app.photomaker.R
import com.app.photomaker.databinding.LayoutHandleImageBinding
import com.app.photomaker.aplication.ApplicationContext.sessionContext
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.tracking.MakerEventDefinition.Companion.EVENT_EV2_G8_CLICK_BTN_FILTER
import com.app.photomaker.tracking.MakerEventDefinition.Companion.EVENT_EV2_G8_CLICK_BTN_ROTATE
import com.app.photomaker.ui.adapters.ViewPagerFragmentAdapter
import com.app.photomaker.ui.screens.edit_screen.EditorVM
import com.app.photomaker.ui.screens.edit_screen.list_img_selected.ListImgSelectedFragment
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@AndroidEntryPoint
class HandleImgFragment: BaseFragment<LayoutHandleImageBinding>() {
    @Inject
    lateinit var bannerAdsManager: BannerAdsManager

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    private val editorVM: EditorVM by activityViewModels()
    private var handleImageMode = HandleImageMode.MODE_CROP
    private var viewpagerHandleImageSelectedAdapter: ViewPagerFragmentAdapter?= null
    private var imageCurrentFilter: ImageSelected? = null
    private var listImageSelected: ArrayList<ImageSelected?> = arrayListOf()
    private var viewpagerDescriptionControlAdapter: ViewPagerFragmentAdapter?= null
    private lateinit var listImgSelectedFragment: ListImgSelectedFragment
    private lateinit var listFilterBrightnessFragment: FilterBrightnessFragment
    private var listImageEdited : HashSet<Int> = hashSetOf()
    private var template : Template? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (handleImageMode == HandleImageMode.MODE_FILTER) {
                handleImageMode = HandleImageMode.MODE_CROP
                refreshDataUI()
            }
            else if (handleImageMode == HandleImageMode.MODE_CROP) {
                viewPagerEditor?.setCurrentItem(EditorTabType.PICK_IMAGE_TAB.position, true)
            }
        }
    }

    /**
     * @param viewPagerEditor is view pager in editor activity
     * Function to navigate SCREENS in activity parent
     */
    private var viewPagerEditor: ViewPager2 ?= null

    companion object {
        fun newInstance(): HandleImgFragment {
            return HandleImgFragment()
        }
    }

    fun setViewPagerEditor(viewPagerEditor: ViewPager2) {
        this.viewPagerEditor = viewPagerEditor
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_handle_image
    }

    override fun initView() {
        initUI()
        refreshDataUI()
        initViewPagerDescriptionControl()
        bannerAdsManager.loadAdsBanner(binding.layoutAds)
    }

    private fun initUI() {
        paddingStatusBar(binding.toolbar.parent)
        val spaceTop = resources.getDimensionPixelSize(R.dimen.dp100)
        val spaceBottom = resources.getDimensionPixelSize(R.dimen.dp252)
        val height = heightScreen - spaceTop - spaceBottom
        val width = (widthScreen * height) / heightScreen - resources.getDimensionPixelOffset(R.dimen.dp10)
        val layoutParam = FrameLayout.LayoutParams(width, MATCH_PARENT)
        layoutParam.setMargins(0, resources.getDimensionPixelSize(R.dimen.dp100), 0, resources.getDimensionPixelSize(R.dimen.dp100))
        layoutParam.gravity = Gravity.CENTER_HORIZONTAL
        binding.viewFilterBrightness.layoutParams = layoutParam
    }

    override fun initListener() {
        binding.layoutControlbar.btnRotate.setSafeOnClickListener {
            eventTrackingManager.sendOtherEvent(EVENT_EV2_G8_CLICK_BTN_ROTATE)
            EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.ROTATE_IMAGE_EVENT))
        }
        binding.layoutControlbar.btnFilter.setSafeOnClickListener {
            eventTrackingManager.sendOtherEvent(EVENT_EV2_G8_CLICK_BTN_FILTER)
            EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.CROP_SAVE_IMAGE_EVENT))
        }
        binding.toolbar.btnBack.setSafeOnClickListener {
            activity?.onBackPressed()
        }
        binding.toolbar.tvPreview.setSafeOnClickListener {
            editorVM.saveTimeGeneratePreviewVideo(System.currentTimeMillis())
            sessionContext.isHandleGoToPreview = true
            editorVM.setUpUIPreviewMode()
            viewPagerEditor?.setCurrentItem(EditorTabType.PREVIEW_VIDEO_TAB.position, true)
            editImageToCreateVideo()
        }
        binding.layoutControlbar.btnDelete.setSafeOnClickListener {
            if(template != null && listImageSelected.size > 2 || template == null){
                val position = binding.viewpagerHandleImage.currentItem
                listImageSelected.removeAt(position)
                viewpagerHandleImageSelectedAdapter?.removeFragmentByPosition(position)
                handleActiveButtonDelete()
                EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.REMOVE_IMAGE_SELECTED_FROM_HANDLE_IMAGE_SCREEN, position))
            }
        }
    }

    private fun editImageToCreateVideo(){
        for(position in 0 until editorVM.imageSelectedList.size){
            if(!listImageEdited.contains(position)){
                val fragmentCropImage = viewpagerHandleImageSelectedAdapter?.getFragmentByPosition(position) as CropImgFragment
                fragmentCropImage.cropAndSaveImage(false, position)
                break
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observerLiveData() {
        editorVM.apply {
            myCurrentImageSelected.observe(this@HandleImgFragment) {
                if (it.isNotEmpty()) {
                    listImageEdited.clear()
                    listImageSelected = it as ArrayList<ImageSelected?>
                    handleActiveButtonDelete()
                    createCropImageFragments(it)
                }
            }
            myCurrentExampleTemplate.observe(this@HandleImgFragment){
                template = it
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun handleActiveButtonDelete(){
        if(template != null && listImageSelected.size < 3){
            binding.layoutControlbar.btnDelete.isEnabled = false
            binding.layoutControlbar.btnDelete.setImageResource(R.drawable.ic_delete_disable)
        }
        else {
            binding.layoutControlbar.btnDelete.isEnabled = true
            binding.layoutControlbar.btnDelete.setImageResource(R.drawable.ic_delete)
        }
    }

    private fun initViewPagerHandleImageSelected() {
        viewpagerHandleImageSelectedAdapter = ViewPagerFragmentAdapter(this)
        binding.viewpagerHandleImage.apply {
            adapter = viewpagerHandleImageSelectedAdapter
            isUserInputEnabled = false
        }
    }

    private fun initViewPagerDescriptionControl() {
        listImgSelectedFragment = ListImgSelectedFragment.newInstance()
        listImgSelectedFragment.setOnAddImageListener { isBackToHandleImageScreen ->
            editorVM.isFromHandleImageScreenToPickImageScreen = isBackToHandleImageScreen
            viewPagerEditor?.currentItem = EditorTabType.PICK_IMAGE_TAB.position
        }
        listImgSelectedFragment.setViewpagerHandleImage(binding.viewpagerHandleImage)
        listFilterBrightnessFragment = FilterBrightnessFragment.newInstance()
        viewpagerDescriptionControlAdapter = ViewPagerFragmentAdapter(this)
        viewpagerDescriptionControlAdapter?.apply {
            addFragment(listImgSelectedFragment)
            addFragment(listFilterBrightnessFragment)
        }
        binding.viewpagerDescriptionControl.apply {
            adapter = viewpagerDescriptionControlAdapter
            isUserInputEnabled = false
            offscreenPageLimit = viewpagerDescriptionControlAdapter?.itemCount ?: 2
        }
    }

    private fun refreshDataUI() {
        if (handleImageMode == HandleImageMode.MODE_CROP) {
            binding.layoutControlbar.apply {
                btnDelete.visibility = View.VISIBLE
                btnRotate.setImageResource(R.drawable.ic_rotate)
                btnRotate.isEnabled = true
                btnFilter.setImageResource(R.drawable.ic_filter)
                btnDelete.visibility = View.VISIBLE
                icIndicatorFilter.visibility = View.INVISIBLE
            }
            binding.viewpagerDescriptionControl.setCurrentItem(DescriptionControlTabType.IMAGE_SELECTED_TAB.position, true)
            binding.viewpagerHandleImage.visibility = View.VISIBLE
            binding.viewFilterBrightness.visibility = View.GONE
            binding.toolbar.tvPreview.visibility = View.VISIBLE
            binding.toolbar.tvTitle.text = getString(R.string.str_title_editor_screen_1)
        }
        if (handleImageMode == HandleImageMode.MODE_FILTER) {
            binding.layoutControlbar.apply {
                btnDelete.visibility = View.VISIBLE
                btnRotate.setImageResource(R.drawable.ic_rotate_disable)
                btnRotate.isEnabled = false
                btnFilter.setImageResource(R.drawable.ic_filter_active)
                btnDelete.visibility = View.GONE
                icIndicatorFilter.visibility = View.VISIBLE
            }
            binding.viewpagerDescriptionControl.setCurrentItem(DescriptionControlTabType.FILTER_BRIGHTNESS_TAB.position, true)
            binding.viewpagerHandleImage.visibility = View.GONE
            binding.viewFilterBrightness.visibility = View.VISIBLE
            binding.toolbar.tvPreview.visibility = View.GONE
            binding.toolbar.tvTitle.text = getString(R.string.str_title_editor_screen_2)
        }
    }

    private fun createCropImageFragments(imageSelected: List<ImageSelected?>) {
        initViewPagerHandleImageSelected()
        viewpagerHandleImageSelectedAdapter?.clearAllData()
        for (image in imageSelected) {
            val bundle = Bundle()
            bundle.putSerializable(ImageSelected.KeyData.IMAGE_SELECTED, image)
            val cropImgFragment = CropImgFragment.newInstance(bundle)
            viewpagerHandleImageSelectedAdapter?.addFragment(cropImgFragment)
        }
        if(imageSelected.isNotEmpty()){
            binding.viewpagerHandleImage.offscreenPageLimit = viewpagerHandleImageSelectedAdapter?.itemCount ?: imageSelected.size
        }
        viewpagerHandleImageSelectedAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        /** Khi màn hình hiển thị thì set lại onBackPress callback để xử lí nghiệp vụ back trên màn hình này*/
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        StatusBarUtils.makeStatusBarTransparentAndLight(activity)
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
                    imageCurrentFilter = event.imageSelected
                    handleImageMode = HandleImageMode.MODE_FILTER
                    refreshDataUI()
                } else {
                    val positionImageSelectedToEdit = event.position
                    if (event.position == 0) {
                        event.imageSelected?.uriResultCutImageInCache?.let {
                            editorVM.setImageThumbToPreview(it)
                        }
                    }
                    if (positionImageSelectedToEdit != null) {
                        editorVM.imageSelectedList[positionImageSelectedToEdit] = event.imageSelected
                        if(positionImageSelectedToEdit == editorVM.imageSelectedList.size - 1){
                            editorVM.setSelectedTemplateVideoData()
                            listImageEdited.clear()
                        } else {
                            listImageEdited.add(positionImageSelectedToEdit)
                            editImageToCreateVideo()
                        }
                    }
                }
            }
            HandleImageEvent.UPDATE_FILTER_BRIGHTNESS_EVENT -> {
                binding.imgFilterBrightness.setImageBitmap(event.filterBrightness?.bitmapImageCropped)
            }
            HandleImageEvent.SAVE_FILTER_BRIGHTNESS_EVENT -> {
                val positionImageChanged = binding.viewpagerHandleImage.currentItem
                imageCurrentFilter = event.imageSelected
                editorVM.updateSelectedImageAfterEdit(imageCurrentFilter, positionImageChanged)
                listImageSelected[positionImageChanged] = event.imageSelected
                EventBus.getDefault().post(HandleImageEvent(HandleImageEvent.UPDATE_IMAGE_FILTER_BRIGHTNESS_IN_LIST_IMAGE_SELECTED,event.imageSelected))
                handleImageMode = HandleImageMode.MODE_CROP
                refreshDataUI()
            }
            HandleImageEvent.RETRY_CROP_IMAGE_TO_PREVIEW_SCREEN ->{
                listImageEdited.clear()
                editImageToCreateVideo()
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}