package com.example.movio.feature.authentication.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.navigation.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signup.EmailVerificationStatus
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/**
 * Covariant
 * */
/*

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : BaseViewModel<SignupCredentials,AuthenticationActions,EmailVerificationStatus>() {

    private val _emailVerified = MutableLiveData<EmailVerificationStatus>()
    val emailVerified: LiveData<EmailVerificationStatus> = _emailVerified

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result

    fun signup(credentials: SignupCredentials){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                _emailVerified.postValue(EmailVerificationStatus.ShouldVerifyEmail)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    fun login(credentials: LoginCredentials){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    private fun newSignup(credentials: SignupCredentials){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                _result.postValue(EmailVerificationStatus.ShouldVerifyEmail)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    override fun postAction(action: AuthenticationActions, data: SignupCredentials) {
        if(action is AuthenticationActions.SignupClicked ){
            newSignup(data)
        }
    }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            authenticationHelper.onSuccess(firebaseUser)
        }
        else{
            _emailVerified.postValue(EmailVerificationStatus.EmailNotVerified)
        }
    }
}*/
















/**
 * Contravariant
 * */

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : BaseViewModel<SignupCredentials,AuthenticationActions,EmailVerificationStatus>() {

    private val _emailVerified = MutableLiveData<EmailVerificationStatus>()
    val emailVerified: LiveData<EmailVerificationStatus> = _emailVerified

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result

    private fun signup(credentials: SignupCredentials){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                _emailVerified.postValue(EmailVerificationStatus.ShouldVerifyEmail)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    fun login(credentials: LoginCredentials){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    private fun newSignup(credentials: SignupCredentials?){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                _result.postValue(EmailVerificationStatus.ShouldVerifyEmail)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
            }
        }
    }

    /*override fun postAction(action: AuthenticationActions, data: SignupCredentials) {
        if(action is AuthenticationActions.SignupClicked ){
            newSignup(data)
        }
    }*/

   /* override fun postAction(action: AuthenticationActions, data: SignupCredentials) {
        *//*if(action is AuthenticationActions.SignupClicked ){
            newSignup(data)
        }*//*
        newSignup(data)
    }*/

    override fun postAction(data: SignupCredentials?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SignupClicked ){
           newSignup(data)
       }
    }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            authenticationHelper.onSuccess(firebaseUser)
        }
        else{
            _emailVerified.postValue(EmailVerificationStatus.EmailNotVerified)
        }
    }

}
