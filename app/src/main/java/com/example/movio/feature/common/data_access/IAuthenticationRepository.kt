package com.example.movio.feature.common.data_access

import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import com.google.firebase.auth.FirebaseUser

interface IAuthenticationRepository {
    suspend fun signup(credentials: SignupCredentials?) : FirebaseUser?

    suspend fun login(credentials: LoginCredentials?) : FirebaseUser?

    suspend fun signupWithGoogle()

    fun register(launcher: AuthenticationResultCallbackLauncher)

    fun register(componentActivity: ComponentActivity)

    fun unregister()

    fun getGoogleSignInService() : GoogleSignInService

    suspend fun signupWithTwitter()

    suspend fun signupWithFacebook()
}