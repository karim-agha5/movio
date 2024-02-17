package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword

class FieldValidationViewModelFactory (
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FieldValidationViewModel(
            validateEmail,
            validatePassword
        ) as T
    }
}