package com.app.imagetovideo.ui.screens.edit_screen.set_wallpaper

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.alo.ringo.tracking.base_event.StateDownloadType
import com.app.imagetovideo.R
import com.app.imagetovideo.ads.nativeads.NativeAdsSetSuccessManager
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.DataTemplateVideo
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.data.model.TemplateVideo
import com.app.imagetovideo.data.realm_model.WallpaperDownloaded
import com.app.imagetovideo.databinding.LayoutSetWallpaperBinding
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.eventbus.MessageEvent
import com.app.imagetovideo.ext.CoroutineExt
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.tracking.EventTrackingManager
import com.app.imagetovideo.tracking.MakerEventDefinition.Companion.EVENT_EV2_G2_SET_VIDEO
import com.app.imagetovideo.ui.dialog.DialogChooseTypeSettingBottomSheet
import com.app.imagetovideo.ui.dialog.DialogSetImageWallpaperSuccess
import com.app.imagetovideo.ui.dialog.DialogSetVideoWallpaperSuccess
import com.app.imagetovideo.utils.*
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
import com.app.imagetovideo.ui.screens.edit_screen.EditorVM
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
class SetWallpaperFragment : BaseFragment<LayoutSetWallpaperBinding>() {
    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var nativeAdsSetSuccessManager: NativeAdsSetSuccessManager

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    val editorVM: EditorVM by activityViewModels()
    var absolutePathFile: String? = null
    lateinit var movieRender: GLSurfaceMovieRenderer
    var templateVideo: TemplateVideo?= null
    var dataTemplateVideo: DataTemplateVideo?= null
    var photoMoviePlayer: PhotoMoviePlayer? = null
    var photoSource: PhotoSource?= null
    var photoMovie: PhotoMovie<*>?= null
    var setVideoWallpaperSuccessDialog: DialogSetVideoWallpaperSuccess? = null
    var chooseTypeSettingBottomSheet: DialogChooseTypeSettingBottomSheet? = null
    var setImageWallpaperSuccessDialog: DialogSetImageWallpaperSuccess? = null
    var objectImageVideoCreated : WallpaperDownloaded?= null
    private var template: Template? = null
    val photoDataList = ArrayList<PhotoData>()
    private val onBackPressedListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
                activity?.finish()
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

    override fun getContentLayout(): Int {
        return R.layout.layout_set_wallpaper
    }

    companion object {
        fun newInstance(template: Template? = null): SetWallpaperFragment {
            val setWallpaperFragment = SetWallpaperFragment()
            if (template != null) {
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_TEMPLATE, template)
                setWallpaperFragment.arguments = bundle
            }
            return setWallpaperFragment
        }
    }

    override fun initView() {
        StatusBarUtils.makeStatusBarTransparentAndDark(requireActivity())
        initToolbar()
        initBottomSheet()
        movieRender = GLTextureMovieRender(binding.glTexture)
        photoMoviePlayer = PhotoMoviePlayer(requireContext())
        photoMoviePlayer?.apply {
            setMovieRenderer(movieRender)
            setMovieListener(movieListener)
            setLoop(true)
            setOnPreparedListener(preparePhotoMovieListener)
        }
    }

    private fun initBottomSheet() {
        chooseTypeSettingBottomSheet = DialogChooseTypeSettingBottomSheet(onSettingImage = {
            setWallpaper(absolutePathFile.toString(), it)
        })

        setImageWallpaperSuccessDialog = DialogSetImageWallpaperSuccess.newInstance()

        setVideoWallpaperSuccessDialog = DialogSetVideoWallpaperSuccess.newInstance()
    }

    override fun initListener() {
        binding.btnSetWallpaper.setSafeOnClickListener {
            binding.glTexture.onPause()
            photoMoviePlayer?.pause()
            setLiveWallpaper()
        }
        binding.btnSetImageWallpaper.setSafeOnClickListener {
            showChooseTypeSettingBottomSheetDialog()
        }
        binding.btnShare.setSafeOnClickListener {
            FileUtils.shareFile(urlFileShare = absolutePathFile?:"", context = requireContext())
        }
        binding.toolbar.btnClose.setSafeOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.SAVED_VIDEO_USER, objectImageVideoCreated))
                EventBus.getDefault().post(MessageEvent(MessageEvent.FINISH_TEMPLATE_ACTIVITY))
                activity?.finish()

        }

        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) {
            if (!it) {
                handleRenameFile()
                binding.viewRenameWallpaper.visibility = View.GONE
            }
        }

        binding.tvNameWallpaper.setSafeOnClickListener {
            binding.viewRenameWallpaper.visibility = View.VISIBLE
            binding.edtRename.requestFocus()
            KeyboardUtils.showSoftKeyboard(binding.edtRename)
        }

        binding.edtRename.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                handleRenameFile()
            }
        }

        binding.edtRename.setOnEditorActionListener { p0, p1, p2 ->
            KeyboardUtils.hideKeyboard(requireActivity())
            handleRenameFile()
            true
        }

        binding.tvSave.setSafeOnClickListener {
            KeyboardUtils.hideKeyboard(requireActivity())
            handleRenameFile()
        }
    }

    private fun handleRenameFile(){
        binding.viewRenameWallpaper.visibility = View.GONE
        if(binding.edtRename.text.toString().trim().isNotEmpty()){
            binding.tvNameWallpaper.text = binding.edtRename.text.toString().trim()
            renameFile()
        }
        binding.edtRename.text = null
    }

    override fun observerLiveData() {
        editorVM.apply {
            myObjectImageOrVideoCreated.observe(this@SetWallpaperFragment) {
                objectImageVideoCreated = it
                absolutePathFile = objectImageVideoCreated?.pathInStorage
                binding.tvNameWallpaper.text = objectImageVideoCreated?.name
            }
            myCurrentDataTemplateVideo.observe(this@SetWallpaperFragment) { data ->
                dataTemplateVideo = data
                val listImageSelected = dataTemplateVideo?.listImageSelected
                templateVideo = data?.templateVideo
                photoDataList.clear()
                if (listImageSelected?.isNotEmpty() == true) {
                    for (img in listImageSelected) {
                        if (img?.uriResultCutImageInCache != null) photoDataList.add(SimplePhotoData(context, img.uriResultCutImageInCache, PhotoData.STATE_LOCAL))
                    }
                    if(listImageSelected.size > 1){
                        binding.itemSetImage.visibility = View.GONE
                        binding.itemSetVideo.visibility = View.VISIBLE
                        binding.glTexture.visibility = View.VISIBLE
                        binding.imgSet.visibility = View.GONE
                        binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_status_set_wallpaper_video)
                        photoMoviePlayer?.setIsWindowMovie(templateVideo?.template)
                        photoDataList.add(photoDataList[0])
                        photoSource = PhotoSource(photoDataList)
                        photoMovie = if(template == null){
                            PhotoMovieFactory.generatePhotoMovie(photoSource, templateVideo?.template)
                        } else {
                            PhotoMovieFactoryUsingTemplate.generatePhotoMovies(template, photoDataList)
                        }
                        photoMoviePlayer?.setDataSource(photoMovie)
                        photoMoviePlayer?.prepare()
                    }
                    else {
                        binding.itemSetImage.visibility = View.VISIBLE
                        binding.itemSetVideo.visibility = View.GONE
                        binding.glTexture.visibility = View.GONE
                        binding.imgSet.visibility = View.VISIBLE
                        binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_status_set_wallpaper_image)

                        GlideHandler.setImageFormUrl(binding.imgSet, dataTemplateVideo?.listImageSelected?.first()?.uriResultCutImageInCache)
                    }
                }
            }
            myCurrentExampleTemplate.observe(this@SetWallpaperFragment){
                template = it
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? {
        return null
    }

    override fun onPause() {
        super.onPause()
        binding.glTexture.onPause()
        photoMoviePlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedListener)
        binding.glTexture.onResume()
        photoMoviePlayer?.start()
    }

    private fun initToolbar() {
        binding.toolbar.container.setPadding(
            0,
            StatusBarUtils.getStatusBarHeight(requireContext()),
            0,
            resources.getDimensionPixelSize(R.dimen.dp20)
        )
        binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_status_set_wallpaper_video)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.SET_WALLPAPER_LIVE.requestCode) {
            if (resultCode == RESULT_OK) {
                if (template != null) {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = template!!.id.toString(),
                        contentType = "template",
                        status = StateDownloadType.OK.value
                    )
                } else {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = "",
                        contentType = "create",
                        status = StateDownloadType.OK.value
                    )
                }
                showSetVideoSuccessDialog()
                editorVM.saveURLWallpaperLiveSet(absolutePathFile ?: "")
            }
            if (resultCode == RESULT_CANCELED && Build.VERSION.SDK_INT <= 27 || resultCode != RESULT_CANCELED && resultCode != RESULT_OK) {
                if (template != null) {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = template!!.id.toString(),
                        contentType = "template",
                        status = StateDownloadType.NOK.value,
                        comment = "Error"
                    )
                } else {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = "",
                        contentType = "create",
                        status = StateDownloadType.NOK.value,
                        comment = "Error"
                    )
                }
                CoroutineExt.runOnMainAfterDelay {
                    ToastUtil.showToast(resources.getString(R.string.txt_set_wallpaper_error), requireContext())
                }
            }
            binding.glTexture.onResume()
            photoMoviePlayer?.start()
        }
    }
}
