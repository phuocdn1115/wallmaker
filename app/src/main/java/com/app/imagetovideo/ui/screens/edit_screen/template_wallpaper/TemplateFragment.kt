package com.app.imagetovideo.ui.screens.edit_screen.template_wallpaper

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.data.model.DataTemplateVideo
import com.app.imagetovideo.data.model.TemplateVideo
import com.app.imagetovideo.databinding.LayoutTemplateBinding
import com.app.imagetovideo.enums.EditorTabType
import com.app.imagetovideo.model.ImageSelected
import com.app.imagetovideo.ui.adapters.TemplateVideoAdapter
import com.app.imagetovideo.ui.screens.edit_screen.EditorVM
import com.app.imagetovideo.utils.BlurViewUtils
import com.app.imagetovideo.utils.MarginItemDecoration
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.extension.heightScreen
import com.app.imagetovideo.utils.extension.widthScreen
import com.app.imagetovideo.utils.setSafeOnClickListener
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

@AndroidEntryPoint
class TemplateFragment: BaseFragment<LayoutTemplateBinding>() {

    private val editorVM: EditorVM by activityViewModels()
    private lateinit var movieRender: GLSurfaceMovieRenderer
    private var photoMovieType = PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS
    private var photoMoviePlayer: PhotoMoviePlayer? = null
    private var oldPositionSelected: Int = 0
    private var templateVideoAdapter: TemplateVideoAdapter?= null
    private var listImageSelected : ArrayList<ImageSelected?>? = arrayListOf()
    private var photoSource : PhotoSource ?= null
    private var photoMovie : PhotoMovie<*> ?= null
    private var dataTemplateVideo: DataTemplateVideo?= null
    private var templateVideo: TemplateVideo?= null

    private var listTemplatesVideo: ArrayList<TemplateVideo?> = arrayListOf()

    private val movieListener = object : IMovieTimer.MovieListener {
        override fun onMovieUpdate(elapsedTime: Int) {}

        override fun onMovieStarted() {}

        override fun onMoviedPaused() {}

        override fun onMovieResumed() {}

        override fun onMovieEnd() {}
    }

    private val onBackPressedListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewPagerEditor?.setCurrentItem(EditorTabType.PREVIEW_VIDEO_TAB.position, true)
        }
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

    private var viewPagerEditor: ViewPager2?= null

    fun setViewPagerEditor(viewPagerEditor: ViewPager2) {
        this.viewPagerEditor = viewPagerEditor
    }

    companion object {
        fun newInstance(): TemplateFragment {
            return TemplateFragment()
        }
    }

    private val templateVM: TemplateVM by viewModels()

    override fun getContentLayout(): Int {
        return R.layout.layout_template
    }

    override fun initView() {
        initDataTemplate()
        initToolbar()
        binding.viewParent.post {
            val heightCurrent = binding.viewParent.height
            val widthSet = (heightCurrent * widthScreen) / heightScreen
            val layoutParamCurrent = binding.viewParent.layoutParams
            layoutParamCurrent.width = widthSet
            binding.viewParent.layoutParams = layoutParamCurrent
        }
        movieRender = GLTextureMovieRender(binding.glTexture)
        photoMoviePlayer = PhotoMoviePlayer(requireContext())
        photoMoviePlayer?.apply {
            setMovieRenderer(movieRender)
            setMovieListener(movieListener)
            setLoop(true)
            setOnPreparedListener(preparePhotoMovieListener)
        }
        initAdapter()
        binding.tvNameTemplate.text = listTemplatesVideo[0]?.name
        BlurViewUtils.setupBlurView(requireActivity(), binding.blurView, binding.root.rootView as ViewGroup)

        photoMoviePlayer?.setOnPreparedListener(object : PhotoMoviePlayer.OnPreparedListener{
            override fun onPreparing(moviePlayer: PhotoMoviePlayer?, progress: Float) {
            }

            override fun onPrepared(
                moviePlayer: PhotoMoviePlayer?,
                prepared: Int,
                total: Int
            ) {
                photoMoviePlayer?.start()
            }

            override fun onError(moviePlayer: PhotoMoviePlayer?) {
                Log.d("PREPARE_LISTENER_ERROR", "PREPARE_LISTENER_ERROR")
            }

        })

    }

    private fun initDataTemplate(){
        listTemplatesVideo = arrayListOf(
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS, true, R.drawable.ic_right_to_left_template, name = resources.getString(R.string.txt_slide_left)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.VERTICAL_TRANS, false, R.drawable.ic_down_to_up_template, name = resources.getString(R.string.txt_slide_up)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.WINDOW, false, R.drawable.ic_window_template, name = resources.getString(R.string.txt_window)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.GRADIENT, false, R.drawable.ic_gradient_template, name = resources.getString(R.string.txt_zoom_in)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.SCALE_TRANS, false, R.drawable.ic_tranlation_template, name = resources.getString(R.string.txt_mix_1)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.THAW, false, R.drawable.ic_thaw_template, name = resources.getString(R.string.txt_mix_2)),
            TemplateVideo(PhotoMovieFactory.PhotoMovieType.SCALE, false, R.drawable.ic_scale_template, name = resources.getString(R.string.txt_zoom_out))
        )
    }

    override fun initListener() {
        /** Save template and back to preview screen */
        binding.toolbarTemplate.tvPreview.setSafeOnClickListener {
            editorVM.currentTemplateSelected = templateVideo
            editorVM.setSelectedTemplateVideoData()
            viewPagerEditor?.setCurrentItem(EditorTabType.PREVIEW_VIDEO_TAB.position, true)
        }
        binding.toolbarTemplate.btnBack.setSafeOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initAdapter(){
        templateVideoAdapter = TemplateVideoAdapter(listTemplatesVideo, onClickItemTemplate = { position ->
            if(position != oldPositionSelected){
                listTemplatesVideo[oldPositionSelected]?.isSelected = false
                templateVideoAdapter?.notifyItemChanged(oldPositionSelected)
                listTemplatesVideo[position]?.isSelected = true
                binding.tvNameTemplate.text = listTemplatesVideo[position]?.name
                templateVideoAdapter?.notifyItemChanged(position)
                oldPositionSelected = position
                templateVideo = listTemplatesVideo[position]
                photoMoviePlayer?.stop()
                photoMoviePlayer?.setIsWindowMovie(listTemplatesVideo[position]?.template)
                photoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, templateVideo?.template)
                photoMoviePlayer?.setDataSource(photoMovie)

                photoMoviePlayer?.prepare()
            }
            binding.layoutTemplates.rvListTemplateVideo.scrollToPosition(oldPositionSelected)
        })
        binding.layoutTemplates.rvListTemplateVideo.setPadding(
            (widthScreen/2) - resources.getDimensionPixelOffset(R.dimen.dp33),
            resources.getDimensionPixelOffset(R.dimen.dp0),
            (widthScreen/2) - resources.getDimensionPixelOffset(R.dimen.dp33),
            resources.getDimensionPixelOffset(R.dimen.dp0)
        )
        binding.layoutTemplates.rvListTemplateVideo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.layoutTemplates.rvListTemplateVideo.addItemDecoration(
            MarginItemDecoration(
                marginLeft = resources.getDimensionPixelOffset(R.dimen.dp5)
            )
        )
        binding.layoutTemplates.rvListTemplateVideo.adapter = templateVideoAdapter
    }

    override fun observerLiveData() {
        editorVM.apply {
            myCurrentDataTemplateVideo.observe(this@TemplateFragment) { data ->
                dataTemplateVideo = data
                listImageSelected = dataTemplateVideo?.listImageSelected
                templateVideo = data?.templateVideo
                if (listImageSelected?.isNotEmpty() == true) {
                    val photoDataList = ArrayList<PhotoData>()
                    for (img in listImageSelected!!) {
                        if (img?.uriInput != null) photoDataList.add(SimplePhotoData(context, img.uriResultCutImageInCache, PhotoData.STATE_LOCAL))
                    }
                    val image = photoDataList[0]
                    photoDataList.add(image)
                    photoMoviePlayer?.setIsWindowMovie(templateVideo?.template)
                    photoMoviePlayer?.stop()
                    photoSource = PhotoSource(photoDataList)
                    photoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, templateVideo?.template)
                    photoMoviePlayer?.setDataSource(photoMovie)
                    photoMoviePlayer?.prepare()
                }
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun initToolbar() {
        binding.toolbarTemplate.parent.setPadding(0, StatusBarUtils.getStatusBarHeight(requireContext()), 0, resources.getDimensionPixelSize(R.dimen.dp20))
        binding.toolbarTemplate.tvTitle.text = getString(R.string.text_toolbar_status_3)
        binding.toolbarTemplate.tvPreview.text = resources.getText(R.string.txt_save_filter)
        binding.toolbarTemplate.btnBack.visibility = View.VISIBLE
        binding.toolbarTemplate.tvPreview.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        /** Khi màn hình hiển thị thì set lại onBackPress callback để xử lí nghiệp vụ back trên màn hình này*/
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedListener)
        StatusBarUtils.makeStatusBarTransparentAndLight(activity)
        binding.glTexture.onResume()
        photoMoviePlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        binding.glTexture.onPause()
        photoMoviePlayer?.pause()
    }
}