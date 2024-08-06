package com.app.photomaker.ui.screens.splash

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import com.app.photomaker.R
import com.app.photomaker.ads.openapp.OpenAppAdsManager
import com.app.photomaker.base.BaseActivity
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.databinding.ActivitySplashBinding
import com.app.photomaker.ext.CoroutineExt
import com.app.photomaker.navigation.NavigationManager
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.ui.screens.main.MainVM
import com.app.photomaker.utils.CommonUtils
import com.app.photomaker.utils.StatusBarUtils
import com.app.photomaker.utils.extension.displayMetrics
import com.app.photomaker.utils.pushDownClickAnimation
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoPlayable
import im.ene.toro.exoplayer.Playable
import im.ene.toro.exoplayer.ToroExo
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var openAppAdsManager: OpenAppAdsManager

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    private val mainVM: MainVM by viewModels()
    private var myPlayable: Playable? = null

    override fun initView() {
        openAppAdsManager.switchOnOff(false)
        StatusBarUtils.makeStatusBarTransparentAndLight(this)
        /**
         * don't load Ads here
         * mainViewModel.loadAds()
         */
        val uri : Uri = RawResourceDataSource.buildRawResourceUri(R.raw.intro_splash)
        myPlayable = ExoPlayable(ToroExo.with(this).defaultCreator, uri, null)
            .also {
                it.prepare(true)
                it.play()
            }
        myPlayable?.playerView = binding.videoView
        binding.videoView.player?.repeatMode = Player.REPEAT_MODE_ONE
        myPlayable?.addEventListener(object : Playable.EventListener {
            override fun onRenderedFirstFrame() {
                binding.videoView.animate().alpha(1f).duration = 300
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
        displayMetrics = CommonUtils.getScreen(baseContext)
        setupView()
    }

    override fun getContentLayout(): Int = R.layout.activity_splash

    override fun observerLiveData() {
        connectionLiveData.observe(this) {}
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun setupView() {
        // Setup animation logo
        if (mainVM.checkIsFirstOpenApp()) {
            CoroutineExt.runOnMainAfterDelay(1200) {
                binding.view.visibility = View.VISIBLE
                binding.tvIntro.visibility = View.VISIBLE
                binding.constraint.visibility = View.VISIBLE
                binding.view.animation = AnimationUtils.loadAnimation(this, R.anim.show_splash)
                binding.tvIntro.animation = AnimationUtils.loadAnimation(this, R.anim.show_splash)
                binding.constraint.animation = AnimationUtils.loadAnimation(this, R.anim.show_splash)
            }
            mainVM.saveFirstOpenApp()
        } else {
            CoroutineExt.runOnMainAfterDelay(1200) {
                binding.view.visibility = View.VISIBLE
                binding.icLogoSplash.visibility = View.VISIBLE
                binding.view.animation = AnimationUtils.loadAnimation(this, R.anim.show_splash)
                binding.icLogoSplash.animation = AnimationUtils.loadAnimation(this, R.anim.show_splash)
                CoroutineExt.runOnMainAfterDelay(500) {
                    navigationManager.navigationToHomeScreen()
                    finish()
                }
            }
        }
    }

    override fun initListener(){
        pushDownClickAnimation(0.95f, binding.btnMakeVideo) {
            binding.btnMakeVideo.isEnabled = false
            navigationManager.navigationToHomeScreen()
            this.finish()
        }
    }
}