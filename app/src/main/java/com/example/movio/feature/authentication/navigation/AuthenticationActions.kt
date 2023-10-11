package com.example.movio.feature.authentication.navigation

import com.example.movio.core.common.Action

sealed class AuthenticationActions : Action {
    object  ToAuthenticationScreen : AuthenticationActions()
    object ToSignInScreen : AuthenticationActions()
    object ToEmailAndPasswordScreen : AuthenticationActions()
    object ToHomeScreen : AuthenticationActions()


    // Experimenting
    object SignupClicked : AuthenticationActions()
    object SignInClicked : AuthenticationActions()
}