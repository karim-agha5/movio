package com.example.movio.feature.common.data_access

import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import com.google.firebase.auth.FirebaseUser

class AuthenticationRepository(
    private val googleSignInService: GoogleSignInService,
    private val twitterAuthenticationService: TwitterAuthenticationService,
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService
) : IAuthenticationRepository{

    override suspend fun signup(credentials: SignupCredentials) : FirebaseUser? = emailAndPasswordAuthenticationService.signup(credentials)

    override suspend fun login(credentials: LoginCredentials): FirebaseUser? = emailAndPasswordAuthenticationService.login(credentials)

    override suspend fun signupWithGoogle() {
        googleSignInService.init()
        googleSignInService.login(null)
    }

    override fun register(launcher: AuthenticationResultCallbackLauncher) = googleSignInService.register(launcher)

    override fun register(componentActivity: ComponentActivity) {
        googleSignInService.register(componentActivity)
        twitterAuthenticationService.register(componentActivity)
    }

    override fun unregister() = googleSignInService.unregister()

    override fun getGoogleSignInService() : GoogleSignInService = googleSignInService

    override suspend fun signupWithTwitter() = twitterAuthenticationService.signup(null)

    @Throws(UnsupportedOperationException::class)
    override suspend fun signupWithFacebook() = throw UnsupportedOperationException()
}