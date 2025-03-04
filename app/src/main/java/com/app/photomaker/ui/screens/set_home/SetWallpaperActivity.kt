package com.app.photomaker.ui.screens.set_home

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.alo.ringo.tracking.base_event.StateDownloadType
import com.app.photomaker.base.Result
import com.app.photomaker.R
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.ads.nativeads.NativeAdsSetSuccessManager
import com.app.photomaker.base.BaseActivity
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.base.handler.GlideHandler
import com.app.photomaker.base.handler.WallpaperHandler
import com.app.photomaker.data.realm_model.WallpaperDownloaded
import com.app.photomaker.databinding.ActivitySetWallpaperBinding
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.enums.WallpaperType
import com.app.photomaker.enums.WallpaperTypeSetting
import com.app.photomaker.eventbus.MessageEvent
import com.app.photomaker.ext.CoroutineExt
import com.app.photomaker.live.LiveWallpaperService
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.tracking.MakerEventDefinition.Companion.EVENT_EV2_G2_SET_VIDEO
import com.app.photomaker.ui.dialog.DialogChooseTypeSettingBottomSheet
import com.app.photomaker.ui.dialog.DialogSetImageWallpaperSuccess
import com.app.photomaker.ui.dialog.DialogSetVideoWallpaperSuccess
import com.app.photomaker.utils.*
import com.app.photomaker.utils.extension.decodeBitmap
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.text.Cue
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoPlayable
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SetWallpaperActivity : BaseActivity<ActivitySetWallpaperBinding>() {

    @Inject
    lateinit var nativeAdsSetSuccessManager: NativeAdsSetSuccessManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    private val setWallpaperActivityVM: SetWallpaperActivityVM by viewModels()
    var setVideoWallpaperSuccessDialog: DialogSetVideoWallpaperSuccess? = null
    var chooseTypeSettingBottomSheet: DialogChooseTypeSettingBottomSheet? = null
    var setImageWallpaperSuccessDialog: DialogSetImageWallpaperSuccess? = null

    private var myPlayable: Playable? = null
    private var dataWallpaper: WallpaperDownloaded?= null

    override fun getContentLayout(): Int = R.layout.activity_set_wallpaper

    override fun initView() {
        setConnectLiveData(connectionLiveData)
        StatusBarUtils.makeStatusBarTransparentAndDark(this)
        getDataFromIntent()
        setupUI()
        setupBottomSheet()
    }

    private fun getDataFromIntent() {
        dataWallpaper = intent.getSerializableExtra(EXTRA_WALLPAPER_DOWNLOADED) as WallpaperDownloaded
    }

    private fun setupUI() {
        GlideHandler.setImageFormUrl(binding.imgSet, dataWallpaper?.pathInStorage)
        binding.toolbar.container.setPadding(0,StatusBarUtils.getStatusBarHeight(this),0, resources.getDimensionPixelSize(R.dimen.dp20))
        if (dataWallpaper?.wallpaperType != WallpaperType.VIDEO_SUGGESTION_TYPE.value) {
            binding.tvNameWallpaper.visibility = View.VISIBLE
            binding.indicator.visibility = View.VISIBLE
            binding.tvNameWallpaper.text = dataWallpaper?.name
        }

        if (dataWallpaper?.wallpaperType == WallpaperType.VIDEO_USER_TYPE.value || dataWallpaper?.wallpaperType == WallpaperType.VIDEO_SUGGESTION_TYPE.value) {
            binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_video)
            binding.itemSetVideo.visibility = View.VISIBLE
            binding.itemSetImage.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
            configWallpaperLive(dataWallpaper?.pathInStorage)
        }
        else if(dataWallpaper?.wallpaperType == WallpaperType.IMAGE_USER_TYPE.value){
            binding.toolbar.tvTitle.text = getString(R.string.text_toolbar_image)
            binding.itemSetVideo.visibility = View.GONE
            binding.itemSetImage.visibility = View.VISIBLE
            binding.playerView.visibility = View.GONE
        }
    }

    private fun setupBottomSheet() {
        chooseTypeSettingBottomSheet = DialogChooseTypeSettingBottomSheet(onSettingImage = { typeSetting ->
            dataWallpaper?.pathInStorage?.let { it -> setWallpaper(it, typeSetting) }
        })

        setVideoWallpaperSuccessDialog = DialogSetVideoWallpaperSuccess.newInstance()

        setImageWallpaperSuccessDialog = DialogSetImageWallpaperSuccess.newInstance()
    }

    override fun initListener() {
        binding.btnSetWallpaper.setSafeOnClickListener {
            setLiveWallpaper()
        }

        binding.btnSetImageWallpaper.setSafeOnClickListener {
            showChooseTyeSettingDialog()
        }

        binding.btnShare.setSafeOnClickListener {
            if (NetworkUtils.isConnected()) {
                val downloadedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dataWallpaper?.pathInStorage?:"")
                FileUtils.shareFile(urlFileShare = downloadedFile.absolutePath, context = this)
            } else showSnackBarNoInternet()
        }

        binding.toolbar.btnClose.setSafeOnClickListener {
            finish()
        }

        setEventListener(
            this,
            KeyboardVisibilityEventListener {
                if (!it) {
                    handleRenameFile()
                    binding.viewRenameWallpaper.visibility = View.GONE
                }
            })

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
            KeyboardUtils.hideKeyboard(this)
            handleRenameFile()
            true
        }

        binding.tvSave.setSafeOnClickListener {
            KeyboardUtils.hideKeyboard(this)
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

    private fun renameFile(){
        dataWallpaper?.name = binding.tvNameWallpaper.text.toString()
        setWallpaperActivityVM.renameImageVideoUserCreated(dataWallpaper)
        EventBus.getDefault().post(MessageEvent(MessageEvent.RENAME_IMAGE_VIDEO, dataWallpaper))
    }

    override fun observerLiveData() {}

    override fun getLayoutLoading(): BaseLoadingView? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.SET_WALLPAPER_LIVE.requestCode) {
            if (resultCode == RESULT_OK) {
                CoroutineExt.runOnMainAfterDelay {
                    showSetVideoSuccessDialog()
                }
                setWallpaperActivityVM.saveURLWallpaperLiveSet(dataWallpaper?.pathInStorage ?: "")
                if (dataWallpaper?.isTemplate == true && dataWallpaper != null) {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = dataWallpaper!!.idTemplate,
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
            }
            if (resultCode == RESULT_CANCELED && Build.VERSION.SDK_INT <= 27 || resultCode != RESULT_CANCELED && resultCode != RESULT_OK) {
                CoroutineExt.runOnMainAfterDelay {
                    ToastUtil.showToast(resources.getString(R.string.txt_set_wallpaper_error) , this)
                }
                if (dataWallpaper?.isTemplate == true && dataWallpaper != null) {
                    eventTrackingManager.sendContentEvent(
                        eventName = EVENT_EV2_G2_SET_VIDEO,
                        contentId = dataWallpaper!!.idTemplate,
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
            }
        }
    }

    private fun configWallpaperLive(urlVideo: String?) {
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
                binding.imgSet.animate().alpha(0f).duration = 300
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

    private fun setLiveWallpaper() {
        if (WallpaperMakerApp.instance.packageManager.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER)) {
            setWallpaperActivityVM.saveWallpaperVideoUrl(dataWallpaper?.pathInStorage)
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(WallpaperMakerApp.instance, LiveWallpaperService::class.java)
            )
            startActivityForResult(intent, RequestCode.SET_WALLPAPER_LIVE.requestCode)
        } else ToastUtil.showToast("DEVICE NOT SUPPORTED", this)
    }

    private fun setWallpaper(path: String, typeSetting: WallpaperTypeSetting) {
        val setWallpaperLiveData = MutableLiveData<Result<Boolean>>()
        setWallpaperLiveData.postValue(Result.InProgress())
        val bitmap = decodeBitmap(path)
        var postValue = false
        if (bitmap != null) WallpaperHandler.setWallpaper(bitmap, typeSetting) { isSuccess ->
            if (isSuccess && !postValue) {
                setWallpaperLiveData.postValue(Result.Success(true))
                postValue = true
                CoroutineExt.runOnMainAfterDelay {
                    showSetImageSuccessDialog()
                }
            } else {
                ToastUtil.showToast(resources.getString(R.string.txt_set_wallpaper_error), this)
                setWallpaperLiveData.postValue(Result.Failure(400, "Error, Try again!"))
            }
        }
    }

    private fun showSetVideoSuccessDialog() {
        setVideoWallpaperSuccessDialog?.show(supportFragmentManager, "dialogSetVideoWallpaperSuccess")
    }

    private fun showChooseTyeSettingDialog() {
        chooseTypeSettingBottomSheet?.show(supportFragmentManager, "chooseTypeSettingBottomSheet")
    }

    private fun showSetImageSuccessDialog() {
        setImageWallpaperSuccessDialog?.show(supportFragmentManager, "dialogSetImageWallpaperSuccess")
    }

    override fun onDestroy() {
        super.onDestroy()
        myPlayable?.playerView = null
        myPlayable?.release()
        myPlayable = null
    }
}