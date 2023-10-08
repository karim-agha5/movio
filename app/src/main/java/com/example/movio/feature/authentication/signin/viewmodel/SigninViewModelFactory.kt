package com.example.movio.feature.authentication.signin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService

class SignInViewModelFactory(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(emailAndPasswordAuthenticationService,authenticationHelper) as T
    }
}