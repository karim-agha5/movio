package com.example.movio.feature.common.data_access

import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials

interface IAuthenticationRepository {
    suspend fun signup(credentials: SignupCredentials)

    suspend fun login(credentials: LoginCredentials)

    suspend fun signupWithGoogle()

    suspend fun signupWithTwitter()

    suspend fun signupWithFacebook()
}