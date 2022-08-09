package com.dev175.privatescreenshots.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class BaseFragment< VB : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    Fragment() {
    protected lateinit var bindings: VB
    protected var baseActivity: BaseActivity<*>? = null

    open fun initUi() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BaseActivity<*>)
            baseActivity = context

    }

    override fun onDetach() {
        super.onDetach()
        baseActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        bindings.lifecycleOwner = this
        return bindings.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


}