package com.example.movio.feature.authentication.signin.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.common.data_access.IAuthenticationRepository

class SignInViewModelFactory(
    private val application: Application,
    private val authenticationRepository: IAuthenticationRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(
            application,
            authenticationRepository
        ) as T
    }
}