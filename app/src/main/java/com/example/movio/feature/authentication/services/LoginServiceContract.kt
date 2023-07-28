package com.example.movio.feature.authentication.services

import com.example.movio.feature.authentication.helpers.BaseCredentials

interface LoginServiceContract<T : BaseCredentials> {
    fun login(credentials: T?)
}