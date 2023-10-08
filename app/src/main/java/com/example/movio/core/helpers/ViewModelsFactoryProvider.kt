package com.example.movio.core.helpers

import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory
import com.google.firebase.auth.FirebaseAuth

object ViewModelsFactoryProvider {

    fun provideAuthenticationViewModelsFactory(
        firebaseAuth: FirebaseAuth,
        authenticationHelper: AuthenticationHelper
    ) : AuthenticationViewModelsFactory{
        return AuthenticationViewModelsFactory(firebaseAuth, authenticationHelper)
    }
}