package com.example.movio.core.interfaces.auth

import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher

interface AuthenticationResultCallbackLauncherRegistrar : ComponentActivityRegistrar{
    public fun register(launcher: AuthenticationResultCallbackLauncher)

    public fun unregister()
}