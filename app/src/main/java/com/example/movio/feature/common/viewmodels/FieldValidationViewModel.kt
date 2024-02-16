package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import com.example.movio.feature.common.use_cases.ValidateEmail

class FieldValidationViewModel(
    private val validateEmail: ValidateEmail
) : ViewModel(){

}