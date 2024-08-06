package com.app.photomaker.ui.screens.main.videos

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.photomaker.R
import com.app.photomaker.ads.nativeads.NativeAdsInHomeManager
import com.app.photomaker.ads.openapp.OpenAppAdsManager
import com.app.photomaker.aplication.ApplicationContext
import com.app.photomaker.base.BaseFragment
import com.app.photomaker.base.BaseLoadingView
import com.app.photomaker.base.ConnectionLiveData
import com.app.photomaker.base.Result
import com.app.photomaker.data.model.Wallpaper
import com.app.photomaker.data.response.DataHomeResponse
import com.app.photomaker.databinding.LayoutVideoBinding
import com.app.photomaker.enums.DataType
import com.app.photomaker.enums.RequestCode
import com.app.photomaker.enums.WallpaperType
import com.app.photomaker.eventbus.MessageEvent
import com.app.photomaker.model.Data
import com.app.photomaker.model.NativeAds
import com.app.photomaker.model.NewVideo
import com.app.photomaker.navigation.NavigationManager
import com.app.photomaker.tracking.EventTrackingManager
import com.app.photomaker.tracking.MakerEventDefinition.Companion.EVENT_EV2_G2_CLICK_CREATE_VIDEO
import com.app.photomaker.tracking.MakerEventDefinition.Companion.EVENT_EV2_G2_CLICK_TEMPLATE
import com.app.photomaker.ui.adapters.VideoInHomeAdapter
import com.app.photomaker.ui.dialog.DialogConfirmDeleteVideo
import com.app.photomaker.ui.dialog.DialogRequestPermissionStorage
import com.app.photomaker.ui.screens.main.MainVM
import com.app.photomaker.utils.ToastUtil
import com.app.photomaker.utils.setSafeOnClickListener
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.math.abs
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class VideosFragment : BaseFragment<LayoutVideoBinding>(){

    @Inject
    lateinit var openAppAdsManager: OpenAppAdsManager

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    @Inject
    lateinit var eventTrackingManager: EventTrackingManager

    @Inject
    lateinit var nativeAdsInHomeManager: NativeAdsInHomeManager

    private val mainVM: MainVM by activityViewModels()
    var requestPermissionStorageDialog: DialogRequestPermissionStorage? = null
    private var dataListVideo = arrayListOf<Data>()
    private lateinit var adapterVideosInHome : VideoInHomeAdapter
    private val listVideoUserCreated = LinkedList<Data>()
    private var currentPositionItemClicked: Int?= 0
    var deleteVideoDialog : DialogConfirmDeleteVideo?= null

    companion object {
        fun newInstance(): VideosFragment {
            return VideosFragment()
        }
    }

    override fun getContentLayout(): Int = R.layout.layout_video

    override fun initView() {
        initRecyclerView()
        initFooterLoadMore()
        handleBackPressed()
    }

    override fun initListener() {
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                binding.collapsingToolbar.title = ""
                binding.viewToolbarCollapsed.visibility = View.VISIBLE
            } else {
                binding.collapsingToolbar.title = resources.getText(R.string.text_tab_videos)
                binding.viewToolbarCollapsed.visibility = View.GONE
            }
        })
        binding.cardViewNewVideoToolBar.setSafeOnClickListener {
            eventTrackingManager.sendContentEvent(eventName = EVENT_EV2_G2_CLICK_CREATE_VIDEO)
            if (checkPermissionReadExternalStorage()) {
                navigationManager.navigationToEditorScreen()
            } else {
                showDialogRequestPermissionStorage()
            }
        }
    }

    private fun initFooterLoadMore() {
        binding.refreshLayout.setHeaderHeight(0F)
        binding.refreshLayout.setFooterHeight(100F)
        binding.refreshLayout.setOnLoadMoreListener {
            binding.refreshLayout.finishLoadMore()
        }
    }

    override fun observerLiveData() {
        mainVM.apply {
            dataHomeResult.observe(this@VideosFragment){ result ->
                when(result){
                    is Result.InProgress ->{
                        if (dataListVideo.isEmpty())
                            showLoading(true)
                    }
                    is Result.Success ->{
                        showLoading(false){
                            myProcessData(result.data)
                            adapterVideosInHome.notifyDataSetChanged()
                        }
                        /**
                         * Check load Ads first time
                         */
                        if(!ApplicationContext.getAdsContext().isLoadAds){
                            ApplicationContext.getAdsContext().isLoadAds = true
                            openAppAdsManager.switchOnOff(true)
                            mainVM.loadAds()
                        }
                    }
                    is Result.Failure -> {}
                    is Result.Error -> {}
                    is Result.Failures<*> -> {}
                }
            }

            listVideoUserResult.observe(this@VideosFragment){
                listVideoUserCreated.addAll(it)
            }
        }

        connectionLiveData.observe(this@VideosFragment) { isConnected ->
            if (isConnected && !ApplicationContext.getAdsContext().isLoadAds) {
                mainVM.loadAds()
            }
        }
    }

    override fun getLayoutLoading(): BaseLoadingView = binding.viewLoading

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                    openAppAdsManager.switchOnOff(false)
                }
            })
    }

    private fun myProcessData(result: DataHomeResponse){
        val dataResponse = result.data.data as ArrayList<Data>
        dataListVideo.add(NewVideo())
        for (position in dataResponse.indices) {
            val itemInDataResponse = dataResponse[position] as Wallpaper
            when (itemInDataResponse.type) {
                DataType.VIDEO_MADE_BY_USER.type -> {
                    listVideoUserCreated.poll()?.let { dataResponse.set(position, it) }
                }
                DataType.NATIVE_ADS.type -> {
                    dataResponse[position] = NativeAds()
                }
                DataType.VIDEO_TEMPLATE_TYPE.type -> {
                    dataResponse[position] = itemInDataResponse.convertToTemplateObject()
                }
            }
        }
        dataListVideo.addAll(listVideoUserCreated)
        dataListVideo.addAll(dataResponse)
    }

    private fun initRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvVideos.layoutManager = layoutManager
        adapterVideosInHome = VideoInHomeAdapter(
            dataList = dataListVideo,
            mContext = requireContext(),
            onClickItemNewVideo = {
                eventTrackingManager.sendContentEvent(eventName = EVENT_EV2_G2_CLICK_CREATE_VIDEO)
                if (checkPermissionReadExternalStorage()) {
                    navigationManager.navigationToEditorScreen()
                } else {
                    showDialogRequestPermissionStorage()
                }
            },
            onClickItemUserImage = { position, userImage ->
                if (NetworkUtils.isConnected()) {
                    currentPositionItemClicked = position
                    navigationManager.navigationToSetWallpaperActivity(userImage.convertToWallpaperDownloaded())
                } else showSnackBarNoInternet()
            },
            onClickItemUserVideo = { position, userVideo ->
                if (NetworkUtils.isConnected()) {
                    currentPositionItemClicked = position
                    navigationManager.navigationToSetWallpaperActivity(userVideo.convertToWallpaperDownloaded())
                } else showSnackBarNoInternet()
            },
            onClickDelete = { position ->
                deleteVideoDialog = DialogConfirmDeleteVideo(onClickDelete = {
                    mainVM.deleteVideoUser(dataListVideo[position])
                    dataListVideo.removeAt(position)
                    adapterVideosInHome.notifyItemRemoved(position)
                    adapterVideosInHome.notifyItemRangeChanged(position, dataListVideo.size)
                })
                deleteVideoDialog?.show(parentFragmentManager, "dialog_confirm_delete")
            },
            onClickTemplate = { position, template ->
                eventTrackingManager.sendContentEvent(
                    eventName = EVENT_EV2_G2_CLICK_TEMPLATE,
                    contentId = template.id.toString()
                )
                navigationManager.navigationToPreviewTemplateActivity(template)
            }
        )
        adapterVideosInHome.setNativeAdsManager(nativeAdsInHomeManager)
        binding.rvVideos.adapter = adapterVideosInHome
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
                navigationManager.navigationToEditorScreen()
                requestPermissionStorageDialog?.dismiss()
            }
        } else {
            ToastUtil.showToast(
                resources.getString(R.string.txt_explain_permission_storage),
                requireContext()
            )
            requestPermissionStorageDialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("RtlHardcoded")
    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        when (event.message) {
            MessageEvent.SAVED_VIDEO_USER ->{
                val eventObj = event.objRealm
                if(eventObj?.wallpaperType == WallpaperType.VIDEO_USER_TYPE.value){
                    event.objRealm?.convertToVideoUser()?.let { dataListVideo.add(1, it) }
                }else if(eventObj?.wallpaperType == WallpaperType.IMAGE_USER_TYPE.value){
                    event.objRealm?.convertToImageUser()?.let { dataListVideo.add(1, it) }
                }
                adapterVideosInHome.notifyItemInserted(1)
                adapterVideosInHome.notifyItemRangeChanged(1, dataListVideo.size)
            }
            MessageEvent.RENAME_IMAGE_VIDEO ->{
                val dataObj = event.objRealm
                val wallpaperType = dataObj?.wallpaperType
                if(wallpaperType == WallpaperType.VIDEO_USER_TYPE.value){
                    dataListVideo[currentPositionItemClicked!!] = dataObj.convertToVideoUser()
                    adapterVideosInHome.notifyItemChanged(currentPositionItemClicked!!, dataObj.convertToVideoUser())
                }else if(wallpaperType == WallpaperType.IMAGE_USER_TYPE.value){
                    dataListVideo[currentPositionItemClicked!!] = dataObj.convertToImageUser()
                    adapterVideosInHome.notifyItemChanged(currentPositionItemClicked!!, dataObj.convertToImageUser())
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}