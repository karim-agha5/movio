package com.example.movio.feature.authentication.signin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movio.core.common.BaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.navigation.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signup.EmailVerificationStatus
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/**
 * Covariant
 * */
/*

class SignInViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : BaseViewModel<LoginCredentials, AuthenticationActions, EmailVerificationStatus>() {

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result


    override fun postAction(action: AuthenticationActions, data: LoginCredentials) {
        if(action is AuthenticationActions.SignInClicked){
            login(data)
        }
    }

    private fun login(credentials: LoginCredentials){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            authenticationHelper.onSuccess(firebaseUser)
        }
        else{
            _result.postValue(EmailVerificationStatus.EmailNotVerified)
        }
    }
}*/






/**
 * Contravariant
 * */


class SignInViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : BaseViewModel<LoginCredentials, AuthenticationActions, EmailVerificationStatus>() {

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result


   /* override fun postAction(action: AuthenticationActions, data: LoginCredentials) {
        if(action is AuthenticationActions.SignInClicked){
            login(data)
        }
    }*/

    override fun postAction(data: LoginCredentials?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SignInClicked){
            login(data)
        }
    }

    private fun login(credentials: LoginCredentials?){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            authenticationHelper.onSuccess(firebaseUser)
        }
        else{
            _result.postValue(EmailVerificationStatus.EmailNotVerified)
        }
    }
}