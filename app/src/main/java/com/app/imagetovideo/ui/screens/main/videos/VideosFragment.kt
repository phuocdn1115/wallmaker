package com.app.imagetovideo.ui.screens.main.videos

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.imagetovideo.R
import com.app.imagetovideo.aplication.ApplicationContext
import com.app.imagetovideo.base.BaseFragment
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.base.Result
import com.app.imagetovideo.data.model.Wallpaper
import com.app.imagetovideo.data.response.DataHomeResponse
import com.app.imagetovideo.databinding.LayoutVideoBinding
import com.app.imagetovideo.enums.DataType
import com.app.imagetovideo.enums.RequestCode
import com.app.imagetovideo.enums.WallpaperType
import com.app.imagetovideo.eventbus.MessageEvent
import com.app.imagetovideo.model.Data
import com.app.imagetovideo.model.NativeAds
import com.app.imagetovideo.model.NewVideo
import com.app.imagetovideo.navigation.NavigationManager
import com.app.imagetovideo.ui.adapters.VideoInHomeAdapter
import com.app.imagetovideo.ui.dialog.DialogConfirmDeleteVideo
import com.app.imagetovideo.ui.dialog.DialogRequestPermissionStorage
import com.app.imagetovideo.ui.screens.main.MainVM
import com.app.imagetovideo.utils.ToastUtil
import com.app.imagetovideo.utils.setSafeOnClickListener
import com.blankj.utilcode.util.NetworkUtils
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.LinkedList
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class VideosFragment : BaseFragment<LayoutVideoBinding>() {


    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isGranted = true
            permissions.forEach {
                if (!it.value)
                    isGranted = false
            }
            Log.i("CHECK_PERMISSION_GRANTED", "$isGranted ")
            if (isGranted) {
                navigationManager.navigationToEditorScreen()
                requestPermissionStorageDialog?.dismiss()
            } else {
                ToastUtil.showToast(
                    resources.getString(R.string.txt_explain_permission_storage),
                    requireContext()
                )
                requestPermissionStorageDialog?.dismiss()
            }
        }

    private val mainVM: MainVM by activityViewModels()
    var requestPermissionStorageDialog: DialogRequestPermissionStorage? = null
    private var dataListVideo = arrayListOf<Data>()
    private lateinit var adapterVideosInHome: VideoInHomeAdapter
    private val listVideoUserCreated = LinkedList<Data>()
    private var currentPositionItemClicked: Int? = 0
    var deleteVideoDialog: DialogConfirmDeleteVideo? = null

    companion object {
        fun newInstance(): VideosFragment {
            return VideosFragment()
        }
    }

    override fun getContentLayout(): Int = R.layout.layout_video

    override fun initView() {
        initRecyclerView()
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
            if (checkPermissionReadExternalStorage()) {
                navigationManager.navigationToEditorScreen()
            } else {
                showDialogRequestPermissionStorage()
            }
        }
    }

    override fun observerLiveData() {
        mainVM.apply {
            dataHomeResult.observe(this@VideosFragment) { result ->
                when (result) {
                    is Result.InProgress -> {
                        Log.i("CHECK_LOG", "START_LOADING")
                        if (dataListVideo.isEmpty())
                            showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false) {
                            Log.i("CHECK_LOG", "RESULT_SUCCESS: ${result.data}")

                            myProcessData(result.data)
                            adapterVideosInHome.notifyDataSetChanged()
                        }
                        /**
                         * Check load Ads first time
                         */
                        if (!ApplicationContext.getAdsContext().isLoadAds) {
                            ApplicationContext.getAdsContext().isLoadAds = true
                            mainVM.loadAds()
                        }
                    }

                    is Result.Failure -> {}
                    is Result.Error -> {}
                    is Result.Failures<*> -> {}
                }
            }


            listVideoUserResult.observe(this@VideosFragment) {
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
                }
            })
    }

    private fun myProcessData(result: DataHomeResponse) {
        val dataResponse =
            if (result.data != null) result.data.data as ArrayList<Data> else arrayListOf()
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
                navigationManager.navigationToNewPreviewTemplateActivity(template)
            }
        )
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
            MessageEvent.SAVED_VIDEO_USER -> {
                val eventObj = event.objRealm
                if (eventObj?.wallpaperType == WallpaperType.VIDEO_USER_TYPE.value) {
                    event.objRealm?.convertToVideoUser()?.let { dataListVideo.add(1, it) }
                } else if (eventObj?.wallpaperType == WallpaperType.IMAGE_USER_TYPE.value) {
                    event.objRealm?.convertToImageUser()?.let { dataListVideo.add(1, it) }
                }
                adapterVideosInHome.notifyItemInserted(1)
                adapterVideosInHome.notifyItemRangeChanged(1, dataListVideo.size)
            }

            MessageEvent.RENAME_IMAGE_VIDEO -> {
                val dataObj = event.objRealm
                val wallpaperType = dataObj?.wallpaperType
                if (wallpaperType == WallpaperType.VIDEO_USER_TYPE.value) {
                    dataListVideo[currentPositionItemClicked!!] = dataObj.convertToVideoUser()
                    adapterVideosInHome.notifyItemChanged(
                        currentPositionItemClicked!!,
                        dataObj.convertToVideoUser()
                    )
                } else if (wallpaperType == WallpaperType.IMAGE_USER_TYPE.value) {
                    dataListVideo[currentPositionItemClicked!!] = dataObj.convertToImageUser()
                    adapterVideosInHome.notifyItemChanged(
                        currentPositionItemClicked!!,
                        dataObj.convertToImageUser()
                    )
                }
            }
        }
        EventBus.getDefault().removeStickyEvent(event)
    }
}