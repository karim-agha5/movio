package com.example.movio.core.helpers

import android.app.Application
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.FirebaseAuth

object ViewModelsFactoryProvider {

    fun provideAuthenticationViewModelsFactory(
        application: Application,
        firebaseAuth: FirebaseAuth,
        authenticationHelper: AuthenticationHelper,
        userManager: UserManager
    ) : AuthenticationViewModelsFactory{
        return AuthenticationViewModelsFactory(application,firebaseAuth, authenticationHelper, userManager)
    }
}