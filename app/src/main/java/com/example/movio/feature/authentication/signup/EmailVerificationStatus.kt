package com.example.movio.feature.authentication.signup

sealed class EmailVerificationStatus{
    object ShouldVerifyEmail: EmailVerificationStatus()
    object EmailNotVerified: EmailVerificationStatus()
    object EmailVerified: EmailVerificationStatus()
}
