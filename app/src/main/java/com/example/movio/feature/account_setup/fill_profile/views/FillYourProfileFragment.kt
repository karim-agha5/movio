package com.example.movio.feature.account_setup.fill_profile.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.movio.core.util.ConstantStrings
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
        binding.actSex.setOnItemClickListener { _, _, pos, id ->

        }
        setupEgPhoneNumberUITextConditions()
        binding.ccp.registerCarrierNumberEditText(binding.etPhoneNumber)
    }

    private fun initSexDropDownMenuUIState(){
        val items = arrayOf(Sex.MALE.sexAsString, Sex.FEMALE.sexAsString)
        binding.actSex.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,items))
        binding.actSex.setText(items[0],false)
    }

    private fun setupEgPhoneNumberUITextConditions(){
        binding.etPhoneNumber.doOnTextChanged { text, start, before, count ->
            if (count > 0 && text?.get(0) == '0' && binding.ccp.selectedCountryNameCode.equals(ConstantStrings.COUNTRY_NAME_CODE_EG)){
                binding.etPhoneNumber.text = null
            }
        }
    }
}