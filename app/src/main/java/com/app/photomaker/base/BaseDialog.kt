package com.app.photomaker.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.app.photomaker.R
import com.app.photomaker.utils.StatusBarUtils

abstract class BaseDialog<BINDING : ViewDataBinding>: DialogFragment() {

    lateinit var binding: BINDING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, getLayoutResource(), container, false)
        if (this.dialog?.window != null) {
            this.dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            this.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            this.dialog?.window?.setDimAmount(0.3f)
        }
        val windowManagerLayoutParams = this.dialog?.window?.attributes
        windowManagerLayoutParams?.gravity = getGravityForDialog()
        this.dialog?.window?.attributes = windowManagerLayoutParams
        init(savedInstanceState, binding.root)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setFullScreenDialog()
    }

    /**
     * Set size for base dialog
     */
    private fun setSizeForDialog(width: Int) {
        if (dialog?.window != null) {
            dialog?.window?.setLayout(
                requireActivity().window.decorView.width * width / 10,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    /**
     * Set size full for base dialog
     */
    protected fun setSizeFullForDialog() {
        if (dialog?.window != null) {
            dialog?.window?.setLayout(
                requireActivity().window.decorView.width,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    protected fun setFullScreenDialog() {
        if (dialog?.window != null) {
            dialog?.window?.setLayout(
                requireActivity().window.decorView.width,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
    }

    protected fun setFullScreenStatusBar() {
        if (dialog?.window != null) {
            dialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    protected fun onBackPress(showInterAds: () -> Unit) {
        dialog?.setOnCancelListener {
            showInterAds.invoke()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

    protected abstract fun getGravityForDialog(): Int

    protected abstract fun getLayoutResource(): Int

    protected abstract fun init(saveInstanceState: Bundle?, view: View?)

    protected abstract fun setUp(view: View?)

    protected fun paddingStatusBar(view: View) {
        view.setPadding(0, StatusBarUtils.getStatusBarHeight(requireContext()), 0, 0)
    }

    override fun show(
        fragmentManager: FragmentManager,
        tag: String?
    ) {
        var transaction =
            fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null) {
            transaction.remove(prevFragment)
        }
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
        //new transaction
        transaction = fragmentManager.beginTransaction()
        show(transaction, tag)
    }

    fun dismissDialog(tag: String?) {
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen)
    }
}