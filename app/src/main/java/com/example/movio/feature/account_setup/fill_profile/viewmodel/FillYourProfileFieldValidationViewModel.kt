package com.example.movio.feature.account_setup.fill_profile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName

class FillYourProfileFieldValidationViewModel(
    private val validateFullName: ValidateFullName
) : ViewModel(){

}