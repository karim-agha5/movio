package com.example.movio.feature.common.data_access

import android.content.Intent
import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import com.google.firebase.auth.FirebaseUser
import com.example.movio.feature.authentication.helpers.AuthenticationHelper

/**
 * TODO methods that authenticate the user should return a [FirebaseUser] instead of relying
 * on [AuthenticationHelper] because it's a confusing contract to have.
 * */
interface IAuthenticationRepository {
    suspend fun signup(credentials: SignupCredentials?)

    suspend fun login(credentials: LoginCredentials?)

    suspend fun signupWithGoogle()

    fun register(launcher: AuthenticationResultCallbackLauncher)

    fun register(componentActivity: ComponentActivity)

    fun unregister()

    suspend fun authenticateWithFirebase(data: Intent?) : Unit = throw UnsupportedOperationException()

    suspend fun signupWithTwitter()

    suspend fun signupWithFacebook()
}