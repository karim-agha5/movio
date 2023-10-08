package com.example.movio.feature.authentication.signup

interface Status
sealed class EmailVerificationStatus : Status{
    object ShouldVerifyEmail: EmailVerificationStatus()
    object EmailNotVerified: EmailVerificationStatus()
    object EmailVerified: EmailVerificationStatus()
}
