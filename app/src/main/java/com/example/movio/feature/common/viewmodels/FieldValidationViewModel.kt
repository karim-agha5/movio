package com.example.movio.feature.common.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy

/**
 * TODO the way the validation is being done is not scalable because it only takes into account
 * two form fields only
 * */
class FieldValidationViewModel(
    private val validateEmail: ValidateEmail,
    private val validatePassword: ValidatePassword
) : ViewModel(){

    /**
     * A MutableSharedFlow is used because the value exposed to the fragments may not change
     * (e.g. multiple failed authentications results in the same subclass of the [ValidationResultState] sealed class).
     * If the value isn't changing, a StateFlow won't update the value the UI is collecting. Therefore, no updates will transmitted to the UI.
     */

    private val _fieldsState =
        MutableSharedFlow<Triple<ValidationResultState,ValidationResultState,Boolean>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    val fieldsState = _fieldsState.asSharedFlow()

    init {
        _fieldsState.tryEmit(Triple(ValidationResultState.Neutral,ValidationResultState.Neutral,false))
    }

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

        _fieldsState.tryEmit(Triple(emailValidationResult,passwordValidationResult,canSignup))
    }
}