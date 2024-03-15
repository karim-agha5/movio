package com.example.movio.core.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.example.movio.core.MovioApplication
import com.example.movio.core.helpers.ViewModelDelegate

abstract class BaseFragment<
        VB : ViewBinding?,
        D: Data,
        ActionType: Action,
        S: Status
        >(
            private val cls: Class<out Fragment>
        ) : Fragment(),LifecycleEventObserver {

    private     var _binding: VB? = null
    protected   val binding get() = _binding!!

    // Must be instantiated lazely
    protected   val movioApplication by lazy { (requireActivity().application as MovioApplication) }

    protected lateinit var viewModel: BaseViewModel<D,ActionType,S>

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            val vm by ViewModelDelegate<D,ActionType,S>(movioApplication,cls)
            viewModel = vm
        }
    }
}
