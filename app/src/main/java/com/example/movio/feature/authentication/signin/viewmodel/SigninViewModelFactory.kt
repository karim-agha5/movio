package com.example.movio.feature.authentication.signin.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.common.data_access.IAuthenticationRepository

class SignInViewModelFactory(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    //private val googleSignInService: GoogleSignInService,
    //private val twitterAuthenticationService: TwitterAuthenticationService,
    //private val authenticationHelper: AuthenticationHelper,
    private val application: Application,
    private val authenticationRepository: IAuthenticationRepository
) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInViewModel(
            //emailAndPasswordAuthenticationService,
            //googleSignInService,
            //twitterAuthenticationService,
            //authenticationHelper,
            application,
            authenticationRepository
        ) as T
    }
}