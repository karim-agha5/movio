package com.example.movio.feature.account_setup.fill_profile.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName

class FillYourProfileFieldValidationViewModelFactory(
    private val validateFullName: ValidateFullName
) : ViewModelProvider.Factory{
   /* override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FillYourProfileFieldValidationViewModel(validateFullName) as T
    }*/
}