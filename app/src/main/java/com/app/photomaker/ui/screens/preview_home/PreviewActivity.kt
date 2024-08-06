package com.app.photomaker.ui.screens.preview_home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.alo.ringo.tracking.DefaultEventDefinition.Companion.EVENT_EV2_G1_REWARD
import com.alo.ringo.tracking.base_event.AdsRewardType
import com.alo.ringo.tracking.base_event.StatusType
import com.app.photomaker.R
import com.app.photomaker.base.Result
import com.app.photomaker.ads.rewarded.RewardedAdsManager
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.base.BaseActivity
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.base.FirebaseManager
import com.app.photomaker.base.handler.GlideHandler
import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.databinding.ActivityPreviewBinding
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.enums.WallpaperType
import com.app.photomaker.navigation.NavigationManager
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.ui.dialog.AskViewAdsAgainBottomSheet
import com.app.photomaker.ui.dialog.AskViewAdsBottomSheet
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.utils.EXTRA_DATA_MODEL
import com.app.photomaker.utils.StatusBarUtils
import com.app.photomaker.utils.ToastUtil
import com.app.photomaker.utils.setSafeOnClickListener
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.metadata.Metadata
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoPlayable
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {

    @Inject
    lateinit var rewardedAdsManager: RewardedAdsManager

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    private val previewActivityVM: PreviewActivityVM by viewModels()
    var askViewAdsBottomSheet: AskViewAdsBottomSheet? = null
    var askViewAdsAgainBottomSheet: AskViewAdsAgainBottomSheet? = null
    var requestPermissionStorageDialog: DialogRequestPermissionStorage? = null
    var fragmentProgressDownloadVideo: FragmentProgressDownloadVideo? = null
    var wallpaperType: WallpaperType? = null
    var dataModel: Wallpaper? = null
    private var myPlayable: Playable? = null
    var urlThumb: String? = null
    private var isDownloadWallpaperSuccess = false

    private val onDismissRewardAdCallBack = object : () -> Unit {
        override fun invoke() {
            showProgressDownloadVideo()
            eventTrackingManager.sendRewardAdsEvent(
                eventName = EVENT_EV2_G1_REWARD,
                contentId = ApplicationContext.getAdsContext().adsRewardInPreviewId,
                inPopup = AdsRewardType.EMPTY.value,
                approve = StatusType.SUCCESS.value,
                status = StatusType.SUCCESS.value
            )
        }
    }

    var timeDelay = 300L
    private fun showProgressDownloadVideo() {
        fragmentProgressDownloadVideo?.show(supportFragmentManager, "fragmentProgressDownloadVideo")
        CoroutineScope(Dispatchers.Main).launch {
            var process = 0
            var isSetThumbSuccess = false
            while (true) {
                process++
                if (process < 100) {
                    if (process % 10 == 0 && dataModel?.url != null && isDownloadWallpaperSuccess) {
                        timeDelay = 50L
                    }
                    fragmentProgressDownloadVideo?.setupPercent(process, 100)
                }
                if (urlThumb != null && !isSetThumbSuccess) {
                    fragmentProgressDownloadVideo?.setupThumb(urlThumb!!)
                    isSetThumbSuccess = true
                }
                if (process > 99 && dataModel?.url != null && isDownloadWallpaperSuccess) {
                    fragmentProgressDownloadVideo?.setupPercent(100, 100)
                    timeDelay = 300L
                    goToSetTab()
                    break
                }
                delay(timeDelay)
            }
        }
    }

    override fun getContentLayout(): Int = R.layout.activity_preview

    override fun initView() {
        setConnectLiveData(connectionLiveData)
        StatusBarUtils.makeStatusBarTransparentAndDark(this)
        setupToolbar()
        getDataIntent()
        initUI()
        setupBottomSheet()
        rewardedAdsManager.setOnAdDismissedFullScreenContentListener {
            onDismissRewardAdCallBack.invoke()
        }
    }

    private fun getDataIntent() {
        dataModel = intent.getSerializableExtra(EXTRA_DATA_MODEL) as Wallpaper?
        urlThumb = dataModel?.minThumbURLString()
        if (dataModel != null) {
            firebaseManager.createShareLinkRingtone(dataModel!!, onSuccess = { dataModel?.urlShare = it }, onFailure = {})
        }
    }

    private fun initUI() {
        setupConfigWallpaperLive(dataModel?.originUrlString())
        GlideHandler.setImageFormUrl(binding.imgThumbPreview, dataModel?.minThumbURLString())
    }

    private fun setupBottomSheet() {
        fragmentProgressDownloadVideo = FragmentProgressDownloadVideo()
        askViewAdsBottomSheet = AskViewAdsBottomSheet(
            onClickViewAds = {
                showRewardedAds()
            },
            onDismiss = {
                showRequestViewAdsAgainDialog()
            }
        )
        askViewAdsAgainBottomSheet = AskViewAdsAgainBottomSheet(
            onClickViewAds = {
                showRewardedAds()
            }
        )
        requestPermissionStorageDialog = DialogRequestPermissionStorage(onClickButtonLater = {
            ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage), this)
            requestPermissionStorageDialog?.dismiss()
        }, onClickButtonRequestPermission = {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode
            )
        })
    }

    override fun initListener() {
        binding.btnDownloadVideo.setSafeOnClickListener {
            isDownloadWallpaperSuccess = false
            if (NetworkUtils.isConnected()) {
                permissionToDownload()
            } else showSnackBarNoInternet()
        }

        binding.toolbar.btnBack.setSafeOnClickListener {
            onBackPressed()
        }

        binding.btnShare.setSafeOnClickListener {
            if (NetworkUtils.isConnected()) {
                dataModel?.urlShare?.let { urlShare -> activityShared(urlShare) }
            } else showSnackBarNoInternet()
        }
    }

    private fun activityShared(link: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun permissionToDownload() {
        if (checkPermissionWriteExternalStorage()) {
            showRequestViewAdsDialog()
        } else {
            showDialogRequestPermissionStorage()
        }
    }

    override fun observerLiveData() {
        previewActivityVM.apply {
            downloadWallpaperVideoResult.observe(this@PreviewActivity) {
                when (it) {
                    is Result.InProgress -> {
                        binding.btnDownloadVideo.isClickable = false
                    }
                    is Result.Success -> {
                        dataModel?.url = it.data
                        isDownloadWallpaperSuccess = true
                        binding.btnDownloadVideo.isClickable = true
                    }
                    is Result.Failure -> {
                    }
                }
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun setupToolbar() {
        binding.toolbar.container.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, resources.getDimensionPixelSize(R.dimen.dp20))
        binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_video)
        binding.toolbar.btnClose.visibility = View.INVISIBLE
        binding.toolbar.btnBack.visibility = View.VISIBLE
    }

    private fun goToSetTab() {
        ToastUtil.showToast(getString(R.string.txt_download_success),this)
        dataModel?.convertToWallpaperDownloaded()
            ?.let {
                navigationManager.navigationToSetWallpaperActivity(it)
            }
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCode.PERMISSION_READ_EXTERNAL_STORAGE.requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestPermissionStorageDialog?.dismiss()
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RequestCode.PERMISSION_WRITE_EXTERNAL_STORAGE.requestCode
            )
        } else {
            if (requestCode == RequestCode.PERMISSION_WRITE_EXTERNAL_STORAGE.requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRequestViewAdsDialog()
                requestPermissionStorageDialog?.dismiss()
            } else {
                requestPermissionStorageDialog?.dismiss()
                ToastUtil.showToast(resources.getString(R.string.txt_explain_permission_storage),this)
            }
        }
    }

    private fun setupConfigWallpaperLive(urlVideo: String?) {
        val mediaUriWallpaperLive = Uri.parse(urlVideo)
        myPlayable = ExoPlayable(ToroExo.with(this).defaultCreator, mediaUriWallpaperLive, null)
            .also {
                it.prepare(true)
                it.play()
            }
        myPlayable?.playerView = binding.playerView
        binding.playerView.player?.repeatMode = Player.REPEAT_MODE_ONE
        myPlayable?.addEventListener(object : Playable.EventListener {
            override fun onRenderedFirstFrame() {
                binding.imgThumbPreview.animate().alpha(0f).duration = 300
            }

            override fun onCues(cues: MutableList<Cue>) {}

            override fun onMetadata(metadata: Metadata) {}

            override fun onLoadingChanged(isLoading: Boolean) {}

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {}

            override fun onRepeatModeChanged(repeatMode: Int) {}

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

            override fun onPositionDiscontinuity(reason: Int) {}

            override fun onSeekProcessed() {}

            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {}
        })
    }

    private fun showRequestViewAdsDialog() {
        askViewAdsBottomSheet?.show(supportFragmentManager, "requestViewAdsBottomSheet")
    }

    private fun showRequestViewAdsAgainDialog() {
        askViewAdsAgainBottomSheet?.show(supportFragmentManager, "requestViewAdsAgainBottomSheet")
    }

    private fun showRewardedAds() {
        rewardedAdsManager.show(this) {
            if (it == null) {
                showProgressDownloadVideo()
            }
            dataModel?.let { it1 -> previewActivityVM.downloadVideo(it1)}
        }
    }

    private fun checkPermissionWriteExternalStorage(): Boolean =
        PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(baseContext, Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun showDialogRequestPermissionStorage(){
        requestPermissionStorageDialog?.show(supportFragmentManager, "DIALOG_REQUEST_PERMISSION")
    }

    override fun onDestroy() {
        super.onDestroy()
        myPlayable?.playerView = null
        myPlayable?.release()
        myPlayable = null
    }
}