package com.example.movio.feature.common.viewmodels

import androidx.lifecycle.ViewModel
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TODO the way the validation is being done is not scalable because it only takes into account
 * two form fields only
 * */
class FieldValidationViewModel(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel(){

    private val _fieldsState =
        MutableStateFlow<Triple<ValidationResultState,ValidationResultState,Boolean>>(Triple(ValidationResultState.Neutral,ValidationResultState.Neutral,false))

    val fieldsState = _fieldsState.asStateFlow()

    fun validate(email: String,password: String){
        val emailValidationResult = validateEmail.execute(email)
        val passwordValidationResult = validatePassword.execute(password)
        var canSignup = false

        if(
            emailValidationResult == ValidationResultState.Success &&
            passwordValidationResult == ValidationResultState.Success
        ){
            canSignup = true
        }

        // TODO consider using updateValue() instead of setValue()
        _fieldsState.value = Triple(emailValidationResult,passwordValidationResult,canSignup)
    }
}