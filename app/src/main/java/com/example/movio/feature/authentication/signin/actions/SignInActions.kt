package com.example.movio.feature.authentication.signin.actions

import com.example.movio.core.common.Action

sealed class SignInActions : Action{
    object SignInClicked : SignInActions()
    object FacebookClicked : SignInActions()
    object GoogleClicked : SignInActions()
    object TwitterClicked : SignInActions()
    object SignupClicked : SignInActions()
    object SuccessAction : SignInActions()
    object FailureAction : SignInActions()
}
