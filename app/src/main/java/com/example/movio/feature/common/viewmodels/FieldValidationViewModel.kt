package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FieldValidationViewModel(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel(){

    private val _emailFieldState = MutableStateFlow<ValidationResultState>(ValidationResultState.Neutral)
    val emailFieldsState = _emailFieldState.asStateFlow()
    fun validate(email: String){
        _emailFieldState.value = validateEmail.execute(email)
    }
}