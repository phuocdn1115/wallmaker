package com.app.imagetovideo.ui.screens.main.profile

import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.databinding.LayoutProfileBinding
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.utils.setSafeOnClickListener
import com.blankj.utilcode.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<LayoutProfileBinding>(){

    @Inject
    lateinit var navigationManager: NavigationManager

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun getContentLayout(): Int = R.layout.layout_profile

    override fun initView() {}

    override fun initListener() {
        binding.tvLabelPrivacyPolicy.setSafeOnClickListener {
            if (NetworkUtils.isConnected()) {
                navigationManager.navigationToPrivacyScreen()
            } else showSnackBarNoInternet()
        }
    }

    override fun observerLiveData() {}

    override fun getLayoutLoading(): BaseLoadingView? = null
}