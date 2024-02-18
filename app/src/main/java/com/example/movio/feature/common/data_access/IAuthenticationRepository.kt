package com.example.movio.feature.common.data_access

import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials

interface IAuthenticationRepository {
    fun signup(credentials: SignupCredentials)

    fun login(credentials: LoginCredentials)

    fun signupWithGoogle()

    fun signupWithTwitter()

    fun signupWithFacebook()
}