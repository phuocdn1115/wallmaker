package com.app.imagetovideo.ui.screens.main

import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseActivity
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.databinding.ActivityMainBinding
import com.app.imagetovideo.enums.HomeTabType
import com.app.imagetovideo.ui.adapters.ViewPagerActivityAdapter
import com.app.imagetovideo.ui.screens.main.profile.ProfileFragment
import com.app.imagetovideo.ui.screens.main.videos.VideosFragment
import com.app.imagetovideo.utils.CommonUtils
import com.app.imagetovideo.utils.StatusBarUtils
import com.app.imagetovideo.utils.extension.displayMetrics
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var mConnectionLiveData: ConnectionLiveData

    private val mainVM: MainVM by viewModels()
    private lateinit var videoFragment: VideosFragment
    private lateinit var profileFragment: ProfileFragment
    private var myViewpagerMainAdapter: ViewPagerActivityAdapter ?= null

    private val onClickItemInBottomNavigation =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_videos -> {
                    binding.vpMain.currentItem = HomeTabType.VIDEOS_TAB.position
                }
                R.id.menu_profile -> {
                    binding.vpMain.currentItem = HomeTabType.PROFILE_TAB.position
                }
            }
            return@OnItemSelectedListener true
        }

    private val onClickItemReselectedInBottomNavigation =
        NavigationBarView.OnItemReselectedListener { item ->
            when(item.itemId){
                R.id.menu_videos -> {
                    videoFragment.apply {
                        binding.rvVideos.scrollToPosition(0)
                        binding.appbar.setExpanded(true)
                    }
                }
                R.id.menu_profile -> {

                }
            }
        }

    override fun getContentLayout(): Int = R.layout.activity_main

    override fun initView() {
        setConnectLiveData(mConnectionLiveData)
        displayMetrics = CommonUtils.getScreen(baseContext)
        mainVM.getListVideoUser()
        StatusBarUtils.makeStatusBarTransparentAndDark(this)
        setupViewPager()
    }

    override fun initListener() {
        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    HomeTabType.VIDEOS_TAB.position -> {
                        binding.bottomNavigationMain.selectedItemId = R.id.menu_videos
                    }
                    HomeTabType.PROFILE_TAB.position -> {
                        binding.bottomNavigationMain.selectedItemId = R.id.menu_profile
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun observerLiveData() {
       
    }

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun setupViewPager() {
        setupFragmentInViewPager()
        myViewpagerMainAdapter = ViewPagerActivityAdapter(this)
        myViewpagerMainAdapter?.apply {
            addFragment(videoFragment)
            addFragment(profileFragment)
        }
        binding.bottomNavigationMain.setOnItemSelectedListener(onClickItemInBottomNavigation)
        binding.bottomNavigationMain.setOnItemReselectedListener(onClickItemReselectedInBottomNavigation)
        binding.vpMain.adapter = myViewpagerMainAdapter
        binding.vpMain.offscreenPageLimit = myViewpagerMainAdapter?.itemCount ?: 1
    }

    private fun setupFragmentInViewPager() {
        videoFragment = VideosFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()
    }
}