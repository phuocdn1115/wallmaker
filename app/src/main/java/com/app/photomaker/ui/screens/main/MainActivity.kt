package com.app.photomaker.ui.screens.main

import android.os.RemoteException
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.app.photomaker.R
import com.app.photomaker.WallpaperMakerApp
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.base.BaseActivity
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.base.FirebaseManager
import com.app.photomaker.databinding.ActivityMainBinding
import com.app.photomaker.enums.HomeTabType
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.ui.adapters.ViewPagerActivityAdapter
import com.app.photomaker.ui.screens.main.profile.ProfileFragment
import com.app.photomaker.ui.screens.main.videos.VideosFragment
import com.app.photomaker.utils.CommonUtils
import com.app.photomaker.utils.StatusBarUtils
import com.app.photomaker.utils.extension.displayMetrics
import com.app.photomaker.utils.extension.getDeviceId
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var mConnectionLiveData: ConnectionLiveData

    @Inject
    lateinit var mFirebaseManager: FirebaseManager

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    /**tracking install from Google PLay*/
    lateinit var installReferrerClient: InstallReferrerClient
    private val mUtmCampaign = "utm_campaign"
    private val mUtmSource = "utm_source"
    private val mUtmMedium = "utm_medium"
    private val mUtmTerm = "utm_term"
    private val mUtmContent = "utm_content"
    val sources = arrayOf(mUtmCampaign, mUtmSource, mUtmMedium, mUtmTerm, mUtmContent)

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
        getDataInstallTracking()
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
        mainVM.installerIfNeedLiveData.observe(this@MainActivity) {}
    }

    private fun getDataInstallTracking() {
        installReferrerClient = InstallReferrerClient.newBuilder(this).build()
        installReferrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = installReferrerClient.installReferrer
                            val referrerUrl: String = response.installReferrer
                            val referrerClickTime = response.referrerClickTimestampSeconds
                            val appInstallTime = response.installBeginTimestampSeconds
                            val referrer = response.installReferrer

                            val getParams = getHashMapFromQuery(referrerUrl)
                            var utmCampaign: String? = ""
                            var utmSource: String? = ""
                            var utmMedium: String? = ""
                            var utmTerm: String? = ""
                            var utmContent: String? = ""

                            for (sourceType in sources) {
                                val source = getParams[sourceType]
                                if (source != null) {
                                    when (sourceType) {
                                        this@MainActivity.mUtmCampaign -> {
                                            utmCampaign = source
                                        }
                                        this@MainActivity.mUtmMedium -> {
                                            utmMedium = source
                                        }
                                        this@MainActivity.mUtmSource -> {
                                            utmSource = source
                                        }
                                        this@MainActivity.mUtmTerm -> {
                                            utmTerm = source
                                        }
                                        this@MainActivity.mUtmContent -> {
                                            utmContent = source
                                        }
                                        else -> {}
                                    }
                                }
                            }
                            mainVM.callApiCheckingInstallerIfNeed(
                                utmSource = utmSource,
                                utmCampaign = utmCampaign,
                                utmContent = utmContent,
                                utmMedium = utmMedium,
                                utmTerm = utmTerm
                            )
                            eventTrackingManager.setInstallationTracking(
                                utmMedium = utmMedium.toString(),
                                utmSource = utmSource.toString(),
                                mobileId = WallpaperMakerApp.getContext().getDeviceId(),
                                country = ApplicationContext.getNetworkContext().countryKey
                            )
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        } finally {
                            installReferrerClient.endConnection()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {}
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {}
                }
            }

            override fun onInstallReferrerServiceDisconnected() {}
        })
    }

    fun getHashMapFromQuery(query: String): Map<String, String> {
        val queryPairs: MutableMap<String, String> = LinkedHashMap()
        val pairs = query.split("&".toRegex()).toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
                URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }
        return queryPairs
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