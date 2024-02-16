package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.common.use_cases.ValidateEmail

class FieldValidationViewModelFactory (
    private val validateEmail: ValidateEmail
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FieldValidationViewModel(
            validateEmail
        ) as T
    }
}