package com.example.movio.feature.authentication.signup.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService

class SignupViewModelFactory(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    private val application: Application
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignupViewModel(emailAndPasswordAuthenticationService,authenticationHelper,application) as T
    }
}