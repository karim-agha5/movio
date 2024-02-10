package com.example.movio.feature.authentication.signup.actions

import com.example.movio.core.common.Action
import com.example.movio.feature.authentication.signin.actions.SignInActions

sealed class SignupActions : Action{
    object SignupClicked : SignupActions()
    object FacebookClicked : SignupActions()
    object GoogleClicked : SignupActions()
    object TwitterClicked : SignupActions()
    object SignInClicked : SignupActions()
}
