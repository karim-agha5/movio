package com.example.movio.feature.splash.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.common.helpers.UserManager

class SplashViewModelFactory(
    private val userManager: UserManager,
    private val application: Application
    ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(userManager, application) as T
    }
}