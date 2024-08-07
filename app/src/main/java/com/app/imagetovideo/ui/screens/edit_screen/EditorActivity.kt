package com.app.imagetovideo.ui.screens.edit_screen

import androidx.activity.viewModels
import com.app.imagetovideo.R
import com.app.imagetovideo.base.BaseActivity
import com.app.imagetovideo.base.BaseLoadingView
import com.app.imagetovideo.base.ConnectionLiveData
import com.app.imagetovideo.data.model.Template
import com.app.imagetovideo.databinding.ActivityEditorBinding
import com.app.imagetovideo.ui.adapters.ViewPagerActivityAdapter
import com.app.imagetovideo.ui.screens.edit_screen.handle_img.HandleImgFragment
import com.app.imagetovideo.ui.screens.edit_screen.pick_img.PickImageFragment
import com.app.imagetovideo.ui.screens.edit_screen.preview_wallpaper.PreviewVideoFragment
import com.app.imagetovideo.ui.screens.edit_screen.set_wallpaper.SetWallpaperFragment
import com.app.imagetovideo.ui.screens.edit_screen.template_wallpaper.TemplateFragment
import com.app.imagetovideo.utils.EXTRA_TEMPLATE
import com.app.imagetovideo.utils.StatusBarUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditorActivity : BaseActivity<ActivityEditorBinding>() {
    @Inject
    lateinit var connectionLiveData: ConnectionLiveData

    private val editorVM: EditorVM by viewModels()
    private lateinit var pickImageFragment: PickImageFragment
    private lateinit var handleImgFragment: HandleImgFragment
    private lateinit var previewFragment: PreviewVideoFragment
    private lateinit var templateFragment: TemplateFragment
    private lateinit var setWallpaperFragment: SetWallpaperFragment
    private var viewpagerEditorAdapter: ViewPagerActivityAdapter?= null
    private var template: Template? = null
    override fun getContentLayout(): Int {
        return R.layout.activity_editor
    }

    override fun initView() {
        setConnectLiveData(connectionLiveData)
        StatusBarUtils.makeStatusBarTransparentAndLight(this)
        getDataFromIntent()
        initViewPager()
    }

    override fun initListener() {}

    override fun observerLiveData() {}

    override fun getLayoutLoading(): BaseLoadingView? = null

    private fun getDataFromIntent() {
        if (intent.hasExtra(EXTRA_TEMPLATE)) {
            template = intent.getSerializableExtra(EXTRA_TEMPLATE) as Template
            editorVM.setExampleTemplate(template)
        }
    }

    private fun initViewPager() {
        initFragmentInViewPager()
        viewpagerEditorAdapter = ViewPagerActivityAdapter(this)
        viewpagerEditorAdapter?.apply {
            addFragment(pickImageFragment)
            addFragment(handleImgFragment)
            addFragment(previewFragment)
            addFragment(templateFragment)
            addFragment(setWallpaperFragment)
        }
        binding.vpEditor.adapter = viewpagerEditorAdapter
        binding.vpEditor.offscreenPageLimit = viewpagerEditorAdapter?.itemCount ?: 5
        binding.vpEditor.isUserInputEnabled = false
    }


    private fun initFragmentInViewPager() {
        pickImageFragment = PickImageFragment.newInstance()
        pickImageFragment.setViewPagerEditor(binding.vpEditor)
        handleImgFragment = HandleImgFragment.newInstance()
        handleImgFragment.setViewPagerEditor(binding.vpEditor)
        previewFragment = PreviewVideoFragment.newInstance()
        previewFragment.setViewPagerEditor(binding.vpEditor)
        templateFragment = TemplateFragment.newInstance()
        templateFragment.setViewPagerEditor(binding.vpEditor)
        setWallpaperFragment = SetWallpaperFragment.newInstance()
    }
}