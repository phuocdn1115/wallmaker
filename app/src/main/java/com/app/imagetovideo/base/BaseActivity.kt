package com.app.imagetovideo.base

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.imagetovideo.R
import com.app.imagetovideo.utils.KeyboardUtils.hideKeyboard
import com.app.imagetovideo.utils.SnackBarUtils
import com.app.imagetovideo.utils.StatusBarUtils
import com.blankj.utilcode.util.NetworkUtils

abstract class BaseActivity<BINDING : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding: BINDING
    private var connectionLiveData: ConnectionLiveData? = null
    private var connectInternet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = DataBindingUtil.setContentView(this, getContentLayout())
        initView()
        getLayoutLoading()
        initListener()
        observerLiveData()
        showConnectInternet()
    }

    abstract fun getContentLayout(): Int

    abstract fun initView()

    abstract fun initListener()

    abstract fun observerLiveData()

    abstract fun getLayoutLoading(): BaseLoadingView?

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard(this)
    }

    /**@param event detect clear focus edittext when touch outside */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view: View? = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard(this)
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private var timeStartLoading: Long = 0
    private val timeDelayLoading: Long = 2000 // miliseconds
    private var timeNeedForDelay: Long = 0
    fun showLoading(isLoading: Boolean, callback: (() -> Unit?)? = null) {
        if (isLoading) timeStartLoading = System.currentTimeMillis()
        if (!isLoading) timeNeedForDelay = timeDelayLoading - (System.currentTimeMillis() - timeStartLoading)
        getLayoutLoading()?.setupLoading(isLoading = isLoading, timeNeedForDelay, callback)
    }

    protected fun setConnectLiveData(connectionLiveData: ConnectionLiveData) {
        this.connectionLiveData = connectionLiveData
    }

    private fun showConnectInternet() {
        connectionLiveData?.observe(this) { isConnected ->
            if (connectInternet != isConnected) {
                if (isConnected) showSnackBarConnectedInternet()
                else showSnackBarNoInternet()
                connectInternet = isConnected
            }
        }
    }

    fun showSnackBarNoInternet() {
        if (!NetworkUtils.isConnected())
            SnackBarUtils.showSnackBarNoInternet(
                view = binding.root,
                message = getString(R.string.text_toast_connect_internet_fail),
                imageResource = R.drawable.ic_internet_disconnect,
                duration = 6000,
                layoutInflater = layoutInflater,
                marginBottom = resources.getDimensionPixelOffset(R.dimen.dp60).toFloat()
            )
    }

    private fun showSnackBarConnectedInternet() {
        SnackBarUtils.showSnackBarNoInternet(
            view = binding.root,
            message = getString(R.string.text_toast_connected_internet),
            imageResource = R.drawable.ic_internet_connected,
            duration = 6000,
            layoutInflater = layoutInflater,
            marginBottom = resources.getDimensionPixelOffset(R.dimen.dp60).toFloat()
        )
    }

    protected fun paddingStatusBar(view: View) {
        view.setPadding(0, StatusBarUtils.getStatusBarHeight(baseContext), 0, 0)
    }

}