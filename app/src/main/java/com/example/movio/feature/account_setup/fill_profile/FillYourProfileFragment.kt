package com.example.movio.feature.account_setup.fill_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.movio.databinding.FragmentFillYourProfileBinding

class FillYourProfileFragment : Fragment() {

    private lateinit var binding: FragmentFillYourProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFillYourProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = arrayOf("Male","Female")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,items)
        binding.actSex.setAdapter(adapter)
    }
}