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
    //val emailFieldsState = _emailFieldState.asStateFlow()

    private val _passwordFieldState = MutableStateFlow<ValidationResultState>(ValidationResultState.Neutral)
    //val passwordFieldState = _passwordFieldState.asStateFlow()

    private val _fieldsState =
        MutableStateFlow<Triple<ValidationResultState,ValidationResultState,Boolean>>(Triple(ValidationResultState.Neutral,ValidationResultState.Neutral,false))

    val fieldsState = _fieldsState.asStateFlow()

    fun validate(email: String,password: String){
        //_emailFieldState.value = validateEmail.execute(email)
        //_passwordFieldState.value = validatePassword.execute(password)

        //_fieldsState.value = Pair(validateEmail.execute(email),validatePassword.execute(password))
        val emailValidationResult = validateEmail.execute(email)
        val passwordValidationResult = validatePassword.execute(password)
        var canSignup = false

        if(
            emailValidationResult == ValidationResultState.Success &&
            passwordValidationResult == ValidationResultState.Success
        ){
            canSignup = true
        }

        _fieldsState.value = Triple(emailValidationResult,passwordValidationResult,canSignup)
    }
}