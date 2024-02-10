package com.example.movio.feature.authentication.signup.status

import com.example.movio.core.common.Status
import com.example.movio.feature.authentication.status.SignInStatus

sealed class SignupStatus : Status {
    class SignupFailed(val throwable: Throwable?) : SignupStatus()
    object ShouldVerifyEmail: SignupStatus()
    object SignedupBefore : SignupStatus()
}