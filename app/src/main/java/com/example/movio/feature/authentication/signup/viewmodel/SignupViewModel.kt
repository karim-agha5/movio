package com.example.movio.feature.authentication.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.common.BaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.status.EmailVerificationStatus
import kotlinx.coroutines.launch

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : BaseViewModel<SignupCredentials, AuthenticationActions, EmailVerificationStatus>() {


    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result


    override fun postAction(data: SignupCredentials?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SignupClicked ){
            signup(data)
        }
    }

    private fun signup(credentials: SignupCredentials?){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                _result.postValue(EmailVerificationStatus.ShouldVerifyEmail)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }
}
