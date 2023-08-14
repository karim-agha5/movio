package com.example.movio.feature.authentication.helpers

data class SignupCredentials(
    override val email: String,
    override val password: String
) : BaseCredentials(email,password)
