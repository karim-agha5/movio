package com.example.movio.feature.authentication.helpers

data class LoginCredentials(
     override val email: String,
     override val password: String
) : BaseCredentials(email,password)
