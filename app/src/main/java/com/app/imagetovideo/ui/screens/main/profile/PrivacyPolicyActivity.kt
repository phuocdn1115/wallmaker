package com.app.imagetovideo.ui.screens.main.profile

import com.app.imagetovideo.BuildConfig
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseActivity
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.databinding.ActivityPrivacyBinding
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.setSafeOnClickListener

class PrivacyPolicyActivity : BaseActivity<ActivityPrivacyBinding>() {

    override fun getContentLayout(): Int = R.layout.activity_privacy

    override fun initView() {
        StatusBarUtils.makeStatusBarTransparentAndLight(this)
        paddingStatusBar(binding.root)
        binding.wvPrivacy.loadUrl(BuildConfig.POLICY_URL)
    }

    override fun initListener() {
        binding.imgBack.setSafeOnClickListener { onBackPressed() }
    }

    override fun observerLiveData() {}

    override fun getLayoutLoading(): BaseLoadingView? = null
}