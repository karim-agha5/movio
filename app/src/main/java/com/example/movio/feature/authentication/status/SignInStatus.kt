package com.example.movio.feature.authentication.status

import com.example.movio.core.common.Status

sealed class SignInStatus : Status {
    class SignInFailed(val throwable: Throwable?): SignInStatus()

    object ShouldVerifyEmail: SignInStatus()
    object EmailNotVerified: SignInStatus()
    object EmailVerified: SignInStatus()
}
