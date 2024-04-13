package com.example.movio.feature.account_setup.fill_profile.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.util.ConstantStrings
import com.example.movio.databinding.FragmentFillYourProfileBinding
import com.example.movio.feature.account_setup.fill_profile.actions.FillYourProfileActions
import com.example.movio.feature.account_setup.fill_profile.models.Profile
import com.example.movio.feature.account_setup.fill_profile.status.FillYourProfileStatus
import com.example.movio.feature.account_setup.fill_profile.status.Sex
import com.example.movio.feature.account_setup.fill_profile.viewmodel.FillYourProfileFieldValidationViewModel
import kotlinx.coroutines.launch

class FillYourProfileFragment :
    BaseFragment<FragmentFillYourProfileBinding, Profile, FillYourProfileActions, FillYourProfileStatus>(FillYourProfileFragment::class.java) {

    private val sexValues = arrayOf(Sex.MALE,Sex.FEMALE)
    private var chosenSex: Sex = sexValues[0]
    private val fieldsViewModel: FillYourProfileFieldValidationViewModel by viewModels {FillYourProfileFieldValidationViewModel.factory}

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFillYourProfileBinding {
        return FragmentFillYourProfileBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSexDropDownMenuUIState()
        setupEgPhoneNumberUITextConditions()
        binding.actSex.setOnItemClickListener { _, _, pos, id -> chosenSex = sexValues[pos] }
        binding.btnContinue.setOnClickListener {
            //viewModel.postAction(getProfile(), FillYourProfileActions.ContinueClicked)
            fieldsViewModel.validate(getProfile())
        }
        binding.ccp.registerCarrierNumberEditText(binding.etPhoneNumber)

        lifecycleScope.launch {
            fieldsViewModel.fullNameUiState.collect{
                Log.i("MainActivity", "full name -> $it")
            }
        }

        lifecycleScope.launch {
            fieldsViewModel.nameTagUiState.collect{
                Log.i("MainActivity", "name tag -> $it")
            }
        }

        lifecycleScope.launch {
            fieldsViewModel.phoneNumberUiState.collect{
                Log.i("MainActivity", "phone number -> $it")
            }
        }
    }

    private fun initSexDropDownMenuUIState() {
        val items = arrayOf(Sex.MALE.sexAsString, Sex.FEMALE.sexAsString)
        binding.actSex.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                items
            )
        )
        binding.actSex.setText(items[0], false)
    }

    private fun setupEgPhoneNumberUITextConditions() {
        binding.etPhoneNumber.doOnTextChanged { text, start, before, count ->
            if (count > 0 && text?.get(0) == '0' && binding.ccp.selectedCountryNameCode.equals(
                    ConstantStrings.COUNTRY_NAME_CODE_EG
                )
            ) {
                binding.etPhoneNumber.text = null
            }
        }
    }

    /**
     * TODO this method assumes that data are filled appropriately without any validation. Add some later.
     * */
    private fun getProfile(): Profile {
        return Profile(
            binding.etFullName.text.toString(),
            binding.etNameTag.text.toString(),
            chosenSex,
            binding.etPhoneNumber.text.toString()
        )
    }
}