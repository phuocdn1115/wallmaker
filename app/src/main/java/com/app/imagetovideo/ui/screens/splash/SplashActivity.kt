package com.app.imagetovideo.ui.screens.splash

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.exoplayer.ExoPlayer
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseActivity
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.databinding.ActivitySplashBinding
import com.app.imagetovideo.ext.CoroutineExt
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.ui.screens.main.MainVM
import com.app.imagetovideo.utils.CommonUtils
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.extension.displayMetrics
import com.app.imagetovideo.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    private val mainVM: MainVM by viewModels()

    override fun initView() {
        StatusBarUtils.makeStatusBarTransparentAndLight(this)
        /**
         * don't load Ads here
         * mainViewModel.loadAds()
         */

        val player = ExoPlayer.Builder(this).build()
        binding.videoView.player = player
        val uri : Uri = Uri.Builder()
            .scheme(ContentResolver. SCHEME_ANDROID_RESOURCE)
            .path(R.raw.intro_splash.toString()).build()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = REPEAT_MODE_ONE
        player.addListener( object : Player.Listener {
            override fun onRenderedFirstFrame() {
                binding.videoView.animate().alpha(1f).duration = 300
            }
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
        binding.btnMakeVideo.setSafeOnClickListener {
            binding.btnMakeVideo.isEnabled = false
            navigationManager.navigationToHomeScreen()
            this.finish()
        }
    }
}