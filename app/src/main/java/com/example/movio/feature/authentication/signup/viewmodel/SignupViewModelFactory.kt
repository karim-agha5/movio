package com.example.movio.feature.authentication.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.navigation.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signup.EmailVerificationStatus

class SignupViewModelFactory(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(emailAndPasswordAuthenticationService,authenticationHelper) as T
    }
}