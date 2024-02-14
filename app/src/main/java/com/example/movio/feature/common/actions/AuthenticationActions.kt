package com.example.movio.feature.common.actions

import com.example.movio.core.common.Action

// TODO refactor and split this class into multiple classes and each class should hold its own actions
sealed class AuthenticationActions : Action {

    object FromSplashScreen : AuthenticationActions()
    object ToAuthenticationScreen : AuthenticationActions()
    object ToSignInScreen : AuthenticationActions()
    object ToSignupScreen : AuthenticationActions()
    object ToHomeScreen : AuthenticationActions()
}