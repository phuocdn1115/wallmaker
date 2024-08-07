package com.app.imagetovideo.ui.screens.main.template

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.app.imagetovideo.R
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.base.BaseActivity
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.handler.GlideHandler
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.databinding.ActivityPreviewTemplateBinding
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.eventbus.MessageEvent
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.ui.dialog.DialogRequestPermissionStorage
import com.app.imagetovideo.ui.screens.preview_home.PreviewActivityVM
import com.app.imagetovideo.utils.EXTRA_TEMPLATE
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.ToastUtil
import com.app.imagetovideo.utils.setSafeOnClickListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.text.Cue
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoPlayable
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.DataTemplateVideo
import com.app.imagetovideo.data.model.TemplateVideo
import com.app.imagetovideo.data.sampledata.SampleData
import com.app.imagetovideo.databinding.ActivityNewPreviewTemplateBinding
import com.hw.photomovie.PhotoMovie
import com.hw.photomovie.PhotoMoviePlayer
import com.hw.photomovie.model.PhotoData
import com.hw.photomovie.model.PhotoSource
import com.hw.photomovie.render.GLSurfaceMovieRenderer
import com.hw.photomovie.render.GLTextureMovieRender
import com.hw.photomovie.timer.IMovieTimer

@AndroidEntryPoint
class NewPreviewTemplateActivity : BaseActivity<ActivityNewPreviewTemplateBinding>() {
    private val viewModel: PreviewActivityVM by viewModels()

    @Inject
    lateinit var navigationManager: NavigationManager

    private var myTemplate: Template? = null
    var requestPermissionStorageDialog: DialogRequestPermissionStorage? = null

    lateinit var movieRender: GLSurfaceMovieRenderer
    var templateVideo: TemplateVideo?= null
    var dataTemplateVideo: DataTemplateVideo?= null
    var photoMoviePlayer: PhotoMoviePlayer? = null
    var photoSource: PhotoSource?= null
    var photoMovie: PhotoMovie<*>?= null
    val photoDataList = ArrayList<PhotoData>()

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
            runOnUiThread {
                photoMoviePlayer?.start()
            }
        }
        override fun onError(moviePlayer: PhotoMoviePlayer?) {
            Log.d("CHECK_STATE", "onError: run")
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_preview_template

    override fun initView() {
        initToolbar()
        getDataFromIntent()
        setupUI()
    }

    override fun initListener() {
        binding.tvUseTemplate.setSafeOnClickListener {
            if (checkPermissionReadExternalStorage()) {
                navigationManager.navigationToEditorScreen(template = myTemplate)
            } else {
                showDialogRequestPermissionStorage()
            }
        }

        binding.toolbar.btnBack.setSafeOnClickListener {
            onBackPressed()
        }
    }

    override fun observerLiveData() {

    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun initToolbar() {
        binding.toolbar.container.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, resources.getDimensionPixelSize(R.dimen.dp20))
        binding.toolbar.btnClose.visibility = View.INVISIBLE
        binding.toolbar.btnBack.visibility = View.VISIBLE
        binding.toolbar.tvTitle.text = getString(R.string.str_preview_template)
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(EXTRA_TEMPLATE)) myTemplate = intent.getSerializableExtra(EXTRA_TEMPLATE) as Template
    }

    private fun setupUI() {
        movieRender = GLTextureMovieRender(binding.glTexture)
        photoMoviePlayer = PhotoMoviePlayer(baseContext)
        photoMoviePlayer?.apply {
            setMovieRenderer(movieRender)
            setMovieListener(movieListener)
            setLoop(true)
            setOnPreparedListener(preparePhotoMovieListener)
        }
        configTemplateVideo()
        GlideHandler.setImageFormDrawableResource(binding.imgThumbPreview,  1)
    }

    private fun configTemplateVideo() {
        photoDataList.apply {
            clear()
            addAll(SampleData.samplePhotoDataList(baseContext))
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RequestCode.PERMISSION_WRITE_EXTERNAL_STORAGE.requestCode
                )
            }
            if (requestCode == RequestCode.PERMISSION_WRITE_EXTERNAL_STORAGE.requestCode) {
                navigationManager.navigationToEditorScreen(template = myTemplate)
                requestPermissionStorageDialog?.dismiss()
            }
        } else {
            ToastUtil.showToast(
                resources.getString(R.string.txt_explain_permission_storage),
                this
            )
            requestPermissionStorageDialog?.dismiss()
        }
    }

    @SuppressLint("RtlHardcoded")
    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        when (event.message) {
            MessageEvent.FINISH_TEMPLATE_ACTIVITY ->{
                finish()
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}