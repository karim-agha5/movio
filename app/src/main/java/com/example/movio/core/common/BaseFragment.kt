package com.example.movio.core.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.movio.core.MovioApplication

abstract class BaseFragment<VB : ViewBinding?> : Fragment() {

    private     var _binding: VB? = null
    protected   val binding get() = _binding!!

    // Must be instantiated lazely
    protected   val movioApplication by lazy { (requireActivity().application as MovioApplication) }

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

}
