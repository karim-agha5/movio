package com.example.movio.feature.authentication.services

import android.util.Log
import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TwitterAuthenticationService private constructor(
    private val componentActivity: ComponentActivity,
    private val firebaseAuth: FirebaseAuth,
    private val authenticationHelper: AuthenticationHelper
)
    : LoginServiceContract<LoginCredentials>,SignupServiceContract<SignupCredentials> {


    private val provider = OAuthProvider.newBuilder("twitter.com")

    companion object{
        @Volatile
        private var instance: TwitterAuthenticationService? = null

        fun getInstance(
            componentActivity: ComponentActivity,
            firebaseAuth: FirebaseAuth,
            authenticationHelper: AuthenticationHelper
        ): TwitterAuthenticationService =
             instance ?: synchronized(this){
                instance ?: TwitterAuthenticationService(componentActivity,firebaseAuth,authenticationHelper)
            }
    }

    /**
     * Main-Safe suspend function that either sends the result of a pending result
     * from a previous login/signup attempt or starts a regular login/signup flow.
     * */
    private suspend fun authenticate(){
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if(pendingResultTask != null ){
            // There's already a sign-in/up result, don't go through the authentication flow
            // and retrieve the result
            getPendingResultTaskResult(pendingResultTask)
        }
        else{
            // Start the normal sign-in/up flow
            twitterSigningFlow()
        }
    }

    /**
     * Main-Safe suspend function that starts the regular login/signup flow and sends the result through
     * the authentication result observable.
     * */
    private suspend fun twitterSigningFlow(){
        val pendingResultTask = firebaseAuth
            .startActivityForSignInWithProvider(componentActivity,provider.build())

        getPendingResultTaskResult(pendingResultTask)
    }

    /**
     * Main-safe function that sends a [FirebaseUser] or an [Exception] to the observer.
     * */
    private suspend fun getPendingResultTaskResult(pendingResultTask: Task<AuthResult>){
       withContext(Dispatchers.IO){
           // TODO currently experimenting with runCatching. Look for all the edge cases surrounding
           // the runCatching and async builders.
            val result = runCatching { pendingResultTask.await() }

           when{
               // TODO make sure to receive the OAuth access token
               // so you can call the Twitter API and get basic profile info by calling their REST API
               result.isSuccess -> authenticationHelper.onSuccess(result.getOrNull()?.user)
               result.isFailure -> authenticationHelper.onFailure(result.exceptionOrNull())
           }

        }
    }

    /**
     * Main-safe suspend function that authenticates a Twitter user
     * */
    override suspend fun login(credentials: LoginCredentials?) {
        authenticate()
    }

    // Functions the same as the login() method.
    override suspend fun signup(credentials: SignupCredentials?) {
        authenticate()
    }
}