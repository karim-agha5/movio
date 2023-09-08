package com.example.movio.feature.authentication.signup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signup.EmailVerificationStatus
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : ViewModel() {

    private val _emailVerified = MutableLiveData<EmailVerificationStatus>()
    val emailVerified: LiveData<EmailVerificationStatus> = _emailVerified

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

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            authenticationHelper.onSuccess(firebaseUser)
        }
        else{
            _emailVerified.postValue(EmailVerificationStatus.EmailNotVerified)
        }
    }
}