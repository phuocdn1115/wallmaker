package com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.app.imagetovideo.R
import com.app.imagetovideo.RealmManager
import com.app.imagetovideo.ads.nativeads.NativeAdsInFrameSaving
import com.app.imagetovideo.ads.rewarded.RewardedAdsManager
import com.app.imagetovideo.aplication.ApplicationContext.sessionContext
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.DataTemplateVideo
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.data.model.TemplateVideo
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.databinding.LayoutPreviewBinding
import com.app.imagetovideo.enums.EditorTabType
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.ext.CoroutineExt
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.ui.dialog.AskViewAdsAgainBottomSheet
import com.app.imagetovideo.ui.dialog.AskViewAdsBottomSheet
import com.app.imagetovideo.ui.dialog.DialogCancelPreview
import com.app.imagetovideo.ui.screens.edit_screen.EditorVM
import com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper.*
import com.app.imagetovideo.utils.EXTRA_TEMPLATE
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.ToastUtil
import com.app.imagetovideo.utils.setSafeOnClickListener
import com.app.imagetovideo.video.PhotoMovieFactoryUsingTemplate
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.PhotoMovieFactory
import com.hw.photomovie.PhotoMoviePlayer
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.PhotoSource
import com.hw.photomovie.model.SimplePhotoData
import com.hw.photomovie.render.GLSurfaceMovieRenderer
import com.hw.photomovie.render.GLTextureMovieRender
import com.hw.photomovie.timer.IMovieTimer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class
PreviewVideoFragment: BaseFragment<LayoutPreviewBinding>() {
    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var rewardedAdsManager: RewardedAdsManager

    @Inject
    lateinit var nativeAdsInFrameSaving: NativeAdsInFrameSaving

    @Inject
    lateinit var realmManager: RealmManager

    val editorVM: EditorVM by activityViewModels()
    val previewVM: PreviewVM by activityViewModels()

    var askViewAdsBottomSheet: AskViewAdsBottomSheet? = null
    var askViewAdsAgainBottomSheet: AskViewAdsAgainBottomSheet? = null
    lateinit var movieRender: GLSurfaceMovieRenderer
    var templateVideo: TemplateVideo?= null
    var dataTemplateVideo: DataTemplateVideo?= null
    var photoMoviePlayer: PhotoMoviePlayer? = null
    var photoSource: PhotoSource?= null
    var photoMovie: PhotoMovie<*>?= null
    var wallpaperType: WallpaperType? = null
    var uriImagePrepareToSaveIfWallpaperIsImage : String?= null
    var cancelPreviewDialog: DialogCancelPreview?= null
    var progressSavingVideoFragment: ProgressSavingVideoFragment?= null
    var template: Template? = null
    var timeGeneratePreviewVideo = 0L

    val photoDataList = ArrayList<PhotoData>()

    private val onDismissRewardAdListener = object : () -> Unit {
        override fun invoke() {
        }
    }

    val onSaveVideoProgressListener = object : SaveVideoProgressListener {
        override fun onSuccess( objectImageVideoFragment: WallpaperDownloaded) {
            editorVM.setSavedFilePath(objectImageVideoFragment)
            ToastUtil.showToast(getString(R.string.text_save_success), requireContext())
            CoroutineExt.runOnMainAfterDelay(500) {
                if(progressSavingVideoFragment?.dialog?.isShowing == true)
                    progressSavingVideoFragment?.dismiss()
                viewPagerEditor?.setCurrentItem(EditorTabType.SET_WALLPAPER_TAB.position, true)
            }
        }

        override fun onProgress(progress: Int, total: Int) {
        }

        override fun onFailure(nameFile: String) {
            Log.d("ON_SAVE_VIDEO_FAILURE", "FAILURE")
            if(progressSavingVideoFragment?.dialog?.isShowing == true)
                progressSavingVideoFragment?.dismiss()

        }
    }

    val onSaveImageProgressListener = object : SaveImageProgressListener {
        override fun onProgress() {

        }

        override fun onSuccess(objectImageVideoFragment: WallpaperDownloaded) {
            editorVM.setSavedFilePath(objectImageVideoFragment)
            ToastUtil.showToast(getString(R.string.text_save_success) ,requireContext())
            CoroutineExt.runOnMainAfterDelay(500) {
                viewPagerEditor?.setCurrentItem(EditorTabType.SET_WALLPAPER_TAB.position, true)
            }
        }

        override fun onFailure(error: String) {
            TODO("Not yet implemented")
        }
    }

    private val movieListener = object : IMovieTimer.MovieListener {
        override fun onMovieUpdate(elapsedTime: Int) {}

        override fun onMovieStarted() {}

        override fun onMoviedPaused() {}

        override fun onMovieResumed() {}

        override fun onMovieEnd() {}
    }

    private val preparePhotoMovieListener = object : PhotoMoviePlayer.OnPreparedListener {
        override fun onPreparing(moviePlayer: PhotoMoviePlayer?, progress: Float) {
            Log.d("CHECK_STATE", "onPreparing: run")
        }
        override fun onPrepared(moviePlayer: PhotoMoviePlayer?, prepared: Int, total: Int) {
            Log.d("CHECK_STATE", "onPrepared: run")
            activity?.runOnUiThread {
                photoMoviePlayer?.start()
            }
        }
        override fun onError(moviePlayer: PhotoMoviePlayer?) {
            Log.d("CHECK_STATE", "onError: run")
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewPagerEditor?.setCurrentItem(EditorTabType.HANDLE_IMAGE_TAB.position, true)
        }
    }
    /**
     * @param viewPagerEditor is view pager in editor activity
     * Function to navigate SCREENS in activity parent
     */
    private var viewPagerEditor: ViewPager2?= null
    companion object {
        fun newInstance(template: Template? = null): PreviewVideoFragment {
            val previewVideoFragment = PreviewVideoFragment()
            if (template != null) {
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_TEMPLATE, template)
                previewVideoFragment.arguments = bundle
            }
            return previewVideoFragment
        }
    }

    fun setViewPagerEditor(viewPagerEditor: ViewPager2) {
        this.viewPagerEditor = viewPagerEditor
    }

    override fun getContentLayout(): Int {
        return R.layout.layout_preview
    }

    override fun initView() {
        initToolbar()
        initBottomSheet()
        progressSavingVideoFragment = ProgressSavingVideoFragment()
        movieRender = GLTextureMovieRender(binding.glTexture)
        photoMoviePlayer = PhotoMoviePlayer(requireContext())
        photoMoviePlayer?.apply {
            setMovieRenderer(movieRender)
            setMovieListener(movieListener)
            setLoop(true)
            setOnPreparedListener(preparePhotoMovieListener)
        }
        rewardedAdsManager.setOnAdDismissedFullScreenContentListener {
            onDismissRewardAdListener.invoke()
        }
    }

    private fun initBottomSheet() {
        askViewAdsBottomSheet = AskViewAdsBottomSheet(
            onClickViewAds = {
                showRewardedAds()
            },
            onDismiss = {
                showAdsRequestAgainBottomSheet()
            }
        )
        askViewAdsAgainBottomSheet = AskViewAdsAgainBottomSheet (
            onClickViewAds = {
                showRewardedAds()
            }
        )
    }

    override fun initListener() {
        binding.toolbar.btnBack.setSafeOnClickListener {
            activity?.onBackPressed()
        }
        binding.layoutPreviewImage.toolbar.btnBack.setSafeOnClickListener {
            activity?.onBackPressed()
        }
        binding.btnChangeTemplate.setSafeOnClickListener {
            editorVM.setSelectedTemplateVideoData()
            viewPagerEditor?.setCurrentItem(EditorTabType.TEMPLATE_VIDEO_TAB.position, true)
        }
        binding.btnSave.setSafeOnClickListener {
            previewVM.setImageThumbSavingVideo(dataTemplateVideo?.listImageSelected?.first()?.uriResultCutImageInCache)
            showAdsRequestBottomSheet()
        }
        binding.layoutPreviewImage.btnSave.setSafeOnClickListener {
            showAdsRequestBottomSheet()
        }
        binding.toolbar.btnClose.setSafeOnClickListener {
            showCancelPreviewDialog()
        }
        binding.layoutPreviewImage.toolbar.btnClose.setSafeOnClickListener {
            showCancelPreviewDialog()
        }
    }

    override fun observerLiveData() {
        editorVM.apply {
            myImageThumbToPreview.observe(this@PreviewVideoFragment) { urlThumb ->
                GlideHandler.setImageFormUrl(binding.imgThumb, urlThumb)
            }
            myCurrentDataTemplateVideo.observe(this@PreviewVideoFragment) { data ->
                if (viewPagerEditor?.currentItem == EditorTabType.PREVIEW_VIDEO_TAB.position && sessionContext.isHandleGoToPreview) {
                    timeGeneratePreviewVideo = System.currentTimeMillis() - editorVM.getTimeGeneratePreviewVideo()
                    sessionContext.isHandleGoToPreview = false
                }

                Log.i("PREVIEW_VIDEO", "observerLiveData: 4")
                binding.layoutLinearProgressIndicator.visibility = View.GONE
                Log.d("PREVIEW_VIDEO_FRAGMENT", "----------------------------------GONE-------------------------------------")
                dataTemplateVideo = data
                val listImageSelected = dataTemplateVideo?.listImageSelected
                templateVideo = data?.templateVideo
                photoDataList.clear()
                if (listImageSelected?.isNotEmpty() == true) {
                    Log.i("PREVIEW_VIDEO", "observerLiveData: 5")
                    for (img in listImageSelected) {
                        Log.i("PREVIEW_VIDEO", "${img?.uriResultCutImageInCache}")
                        if (img?.uriResultCutImageInCache != null) photoDataList.add(SimplePhotoData(context, img.uriResultCutImageInCache, PhotoData.STATE_LOCAL))
                    }
                    if (listImageSelected.size > 1) {
                        wallpaperType = WallpaperType.VIDEO_USER_TYPE
                        binding.layoutPreviewImage.viewParentImagePreview.visibility = View.GONE
                        binding.viewParentVideoPreview.visibility = View.VISIBLE
                        binding.viewControl.visibility = View.VISIBLE
                        binding.imgThumb.visibility = View.GONE
                        photoMoviePlayer?.stop()
                        photoMoviePlayer?.setIsWindowMovie(templateVideo?.template)
                        photoDataList.add(photoDataList[0])
                        photoSource = PhotoSource(photoDataList)
                        photoMovie = if(template == null){

                            Log.i("PREVIEW_VIDEO", "observerLiveData: 6")
                            PhotoMovieFactory.generatePhotoMovie(photoSource, templateVideo?.template)
                        } else {

                            Log.i("PREVIEW_VIDEO", "observerLiveData: 7")
                            PhotoMovieFactoryUsingTemplate.generatePhotoMovies(template, photoDataList)
                        }
                        photoMoviePlayer?.setDataSource(photoMovie)
                        photoMoviePlayer?.prepare()
                    } else {
                        wallpaperType = WallpaperType.IMAGE_USER_TYPE
                        binding.viewParent.visibility = View.GONE
                        binding.layoutPreviewImage.linear.visibility = View.VISIBLE
                        binding.layoutPreviewImage.imgPreview.visibility = View.VISIBLE
                        binding.layoutPreviewImage.viewParentImagePreview.visibility = View.VISIBLE
                        GlideHandler.setImageFormUrl(binding.layoutPreviewImage.imgPreview, dataTemplateVideo?.listImageSelected?.first()?.uriResultCutImageInCache)
                        uriImagePrepareToSaveIfWallpaperIsImage = dataTemplateVideo?.listImageSelected?.first()?.uriResultCutImageInCache
                    }
                }
            }
            myPreviewMode.observe(this@PreviewVideoFragment){
                binding.layoutLinearProgressIndicator.visibility = View.VISIBLE
                Log.d("PREVIEW_VIDEO_FRAGMENT", "----------------------------------VISIBLE-------------------------------------")
                if (it > 1) {
                   binding.layoutPreviewImage.root.visibility = View.GONE
                    binding.viewParent.visibility = View.VISIBLE
                    binding.viewParentVideoPreview.visibility = View.INVISIBLE
                    binding.viewControl.visibility = View.INVISIBLE
                }
                else{
                    binding.layoutPreviewImage.imgPreview.visibility = View.INVISIBLE
                    binding.layoutPreviewImage.root.visibility = View.VISIBLE
                    binding.viewParent.visibility = View.GONE
                }
            }
            myCurrentExampleTemplate.observe(this@PreviewVideoFragment){
                template = it
                if (template != null) {
                    binding.layoutChangeTemplate.visibility = View.GONE
                    binding.spaceOne.visibility = View.VISIBLE
                    binding.spaceTwo.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun initToolbar() {
        binding.toolbar.container.setPadding(0, StatusBarUtils.getStatusBarHeight(requireContext()), 0, resources.getDimensionPixelSize(R.dimen.dp20))
        binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_status_1)
        binding.toolbar.btnBack.visibility = View.VISIBLE

        binding.layoutPreviewImage.toolbar.container.setPadding(0, StatusBarUtils.getStatusBarHeight(requireContext()), 0, resources.getDimensionPixelSize(R.dimen.dp20))
        binding.layoutPreviewImage.toolbar.tvTitle.text = getString(R.string.text_toolbar_status_preview_image)
        binding.layoutPreviewImage.toolbar.btnBack.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        /** Khi màn hình hiển thị thì set lại onBackPress callback để xử lí nghiệp vụ back trên màn hình này*/
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        StatusBarUtils.makeStatusBarTransparentAndDark(activity)
        if(photoSource != null){
            binding.glTexture.onResume()
            photoMoviePlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.glTexture.onPause()
        photoMoviePlayer?.pause()
    }
}