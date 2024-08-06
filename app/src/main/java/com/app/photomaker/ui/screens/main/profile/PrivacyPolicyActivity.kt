package com.app.photomaker.ui.screens.main.profile

import com.app.photomaker.BuildConfig
import com.app.photomaker.R
import com.app.photomaker.base.BaseActivity
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.databinding.ActivityPrivacyBinding
import com.app.photomaker.utils.StatusBarUtils
import com.app.photomaker.utils.setSafeOnClickListener

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