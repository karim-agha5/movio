package com.example.movio.feature.authentication.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.common.data_access.IAuthenticationRepository

class AuthenticationViewModelFactory(
    private val application: Application
    private val authenticationRepository: IAuthenticationRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthenticationViewModel(application,authenticationRepository) as T
    }
}