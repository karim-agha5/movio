package com.example.movio.feature.common.models

import com.example.movio.feature.authentication.helpers.BaseCredentials

data class LoginCredentials(
     override val email: String,
     override val password: String
) : BaseCredentials(email,password)
