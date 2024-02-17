package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow

class FieldValidationViewModel(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel(){

    private val _fieldsState = MutableStateFlow<ValidationResultState>(ValidationResultState.Neutral)
    val fieldsState = _fieldsState

    fun validate(email: String){
        _fieldsState.value = validateEmail.execute(email)
    }
}