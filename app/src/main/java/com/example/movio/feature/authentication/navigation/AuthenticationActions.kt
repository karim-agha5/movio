package com.example.movio.feature.authentication.navigation

import com.example.movio.core.common.Coordinator

sealed class AuthenticationActions : Coordinator.Action{
    object ToHomeScreen : AuthenticationActions()
    object ToEmailAndPasswordScreen : AuthenticationActions()
}