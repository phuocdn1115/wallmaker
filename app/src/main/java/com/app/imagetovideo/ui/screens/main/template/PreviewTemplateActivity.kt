package com.app.imagetovideo.ui.screens.main.template

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject
import com.app.imagetovideo.base.Result

@AndroidEntryPoint
class PreviewTemplateActivity : BaseActivity<ActivityPreviewTemplateBinding>() {
    private val viewModel: PreviewActivityVM by viewModels()

    @Inject
    lateinit var navigationManager: NavigationManager

    private var myTemplate: Template? = null
    private var player: ExoPlayer? = null
    var requestPermissionStorageDialog: DialogRequestPermissionStorage? = null

    override fun getContentLayout(): Int = R.layout.activity_preview_template

    override fun initView() {
        initToolbar()
        getDataFromIntent()
        setupUI()
        myTemplate?.let { viewModel.downloadThumbTemplateReview(it) }
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
        viewModel.apply {
            downloadThumbTemplateResult.observe(this@PreviewTemplateActivity){ result ->
                when(result){
                    is Result.InProgress ->{
                        Log.d("OBSERVER_DATA", "------------------------------------IN PROGRESS")
                    }
                    is Result.Success ->{
                        Log.d("OBSERVER_DATA", "Success - $result")
                        configTemplateVideo(result.data)

                    }
                    is Result.Error ->{
                        Log.d("OBSERVER_DATA", "Error - ${result.exceptionMessage}")
                    }
                    is Result.Failure ->{
                        Log.d("OBSERVER_DATA", "Failure - ${result.failureMessage}")
                    }
                }

            }
        }

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
        else finish()
    }

    private fun setupUI() {
        configTemplateVideo()
        GlideHandler.setImageFormDrawableResource(binding.imgThumbPreview,  1)
    }

    private fun configTemplateVideo(filePath : String ?= null) {
        val mediaUriWallpaperLive = if(filePath == null) Uri.parse("")
//        Uri.parse("${ApplicationContext.getNetworkContext().videoURL}${myTemplate?.originUrlString()}")
        else Uri.parse(filePath)
        player = ExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val uri : Uri = Uri.Builder()
            .scheme(ContentResolver. SCHEME_ANDROID_RESOURCE)
            .path(R.raw.intro_splash.toString()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        player?.addListener( object : Player.Listener {
            override fun onRenderedFirstFrame() {
                Log.i("SPLASH_ACTIVITY", "onRenderedFirstFrame: ")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.i("SPLASH_ACTIVITY", "onIsPlayingChanged: $isPlaying")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
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