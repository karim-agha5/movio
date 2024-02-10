package com.example.movio.feature.common.status

import com.example.movio.core.common.Status

sealed class UserAuthenticationStatus : Status{
    object UserIsLoggedIn : UserAuthenticationStatus()
    object UserIsLoggedOut : UserAuthenticationStatus()
}
