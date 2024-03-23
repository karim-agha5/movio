package com.example.movio.feature.account_setup.fill_profile.views

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.movio.databinding.FragmentFillYourProfileBinding
import com.example.movio.feature.account_setup.fill_profile.status.Sex

class FillYourProfileFragment : Fragment() {

    private lateinit var binding: FragmentFillYourProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFillYourProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSexDropDownMenuUIState()
    }

    private fun initSexDropDownMenuUIState(){
        val items = arrayOf(Sex.MALE.sexAsString, Sex.FEMALE.sexAsString)
        binding.actSex.setAdapter(ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line,items))
        binding.actSex.setText(items[0],false)
    }
}