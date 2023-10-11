package com.example.movio.feature.authentication.status

import com.example.movio.core.common.Status

sealed class EmailVerificationStatus : Status {
    object ShouldVerifyEmail: EmailVerificationStatus()
    object EmailNotVerified: EmailVerificationStatus()
    object EmailVerified: EmailVerificationStatus()
}
