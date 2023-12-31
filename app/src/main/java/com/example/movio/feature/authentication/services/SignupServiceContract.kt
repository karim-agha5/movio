package com.example.movio.feature.authentication.services

import com.example.movio.feature.authentication.helpers.BaseCredentials

interface SignupServiceContract<T : BaseCredentials> {
    suspend fun signup(credentials: T?)
}