package com.example.movio.feature.common.actions

import com.example.movio.core.common.Action

// TODO refactor and split this class into multiple classes and each class should hold its own actions
sealed class AuthenticationActions : Action {
    object  ToAuthenticationScreen : AuthenticationActions()
    object ToSignInScreen : AuthenticationActions()
    object ToEmailAndPasswordScreen : AuthenticationActions()
    object ToHomeScreen : AuthenticationActions()
    object SignupClicked : AuthenticationActions()
    object SignInClicked : AuthenticationActions()
}