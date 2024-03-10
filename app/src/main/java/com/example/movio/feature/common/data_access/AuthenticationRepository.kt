package com.example.movio.feature.common.data_access

import android.content.Intent
import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthenticationRepository(
    private val googleSignInService: GoogleSignInService,
    private val twitterAuthenticationService: TwitterAuthenticationService,
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService
) : IAuthenticationRepository{

    override suspend fun signup(credentials: SignupCredentials?) = withContext(Dispatchers.IO){
        try                     { AuthenticationHelper.onSuccess(emailAndPasswordAuthenticationService.signup(credentials)) }
        catch (ex: Exception)   { AuthenticationHelper.onFailure(ex) }
    }

    override suspend fun login(credentials: LoginCredentials?) = withContext(Dispatchers.IO){
        try                     { AuthenticationHelper.onSuccess(emailAndPasswordAuthenticationService.login(credentials)) }
        catch (ex: Exception)   { AuthenticationHelper.onFailure(ex) }
    }

    override suspend fun signupWithGoogle() {
        googleSignInService.init()
        withContext(Dispatchers.IO){
            try                     { /*googleSignInService.login(null)*/ googleSignInService.authenticate() }
            catch (ex: Exception)   { AuthenticationHelper.onFailure(ex) }
        }
    }

    override fun register(launcher: AuthenticationResultCallbackLauncher) = googleSignInService.register(launcher)

    override fun register(componentActivity: ComponentActivity) {
        googleSignInService.register(componentActivity)
        twitterAuthenticationService.register(componentActivity)
    }

    override fun unregister() = googleSignInService.unregister()

    override suspend fun signupWithTwitter() = withContext(Dispatchers.IO){
        // TODO currently experimenting with runCatching. Look for all the edge cases surrounding the runCatching and async builders.
        val result = runCatching { twitterAuthenticationService.authenticate() }
        when{
            // TODO make sure to receive the OAuth access token so you can call the Twitter API and get basic profile info by calling their REST API
            result.isSuccess -> AuthenticationHelper.onSuccess(result.getOrNull())
            result.isFailure -> AuthenticationHelper.onFailure(result.exceptionOrNull())
        }
    }

    override suspend fun authenticateWithFirebase(data: Intent?) : Unit = withContext(Dispatchers.IO){
        try                     { AuthenticationHelper.onSuccess(googleSignInService.authenticateWithFirebase(data)) }
        catch (ex: Exception)   { AuthenticationHelper.onFailure(ex) }
    }

    @Throws(UnsupportedOperationException::class)
    override suspend fun signupWithFacebook() = throw UnsupportedOperationException()
}