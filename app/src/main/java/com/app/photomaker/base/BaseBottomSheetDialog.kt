package com.app.photomaker.base

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.app.photomaker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
/**
 * Created by thevu2907@gmail.com on 22/02/2022.
 */
abstract class BaseBottomSheetDialog<BINDING : ViewDataBinding>: BottomSheetDialogFragment() {

    lateinit var binding: BINDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, getLayoutResource(), container, false);
        if (this.dialog?.window != null) {
            this.dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            this.dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
        }
        initView(savedInstanceState, binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener(view)
    }

    protected abstract fun getLayoutResource(): Int

    protected abstract fun initView(saveInstanceState: Bundle?, view: View?)

    protected abstract fun initListener(view: View?)

    override fun show(
        fragmentManager: FragmentManager,
        tag: String?
    ) {

        try {
            val transaction = fragmentManager.beginTransaction()
            val prevFragment = fragmentManager.findFragmentByTag(tag)
            if (prevFragment != null) {
                transaction.remove(prevFragment)
            }
            transaction.addToBackStack(null)
            show(transaction, tag)
        } catch (ignored: IllegalStateException) {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }
        super.onSaveInstanceState(outState)
    }
}