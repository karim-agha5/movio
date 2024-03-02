package com.example.movio.feature.authentication.services

import androidx.activity.ComponentActivity
import com.example.movio.core.common.IFederatedAuthentication
import com.example.movio.core.interfaces.auth.ComponentActivityRegistrar
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.IllegalStateException
import kotlin.jvm.Throws

class TwitterAuthenticationService private constructor(
    //private val componentActivity: ComponentActivity,
    private val firebaseAuth: FirebaseAuth,
    private val authenticationHelper: AuthenticationHelper
) : IFederatedAuthentication,
    ComponentActivityRegistrar{


    private val provider = OAuthProvider.newBuilder("twitter.com")
    private lateinit var componentActivity: ComponentActivity

    companion object{
        @Volatile
        private var instance: TwitterAuthenticationService? = null

        fun getInstance(
            //componentActivity: ComponentActivity,
            firebaseAuth: FirebaseAuth,
            authenticationHelper: AuthenticationHelper
        ): TwitterAuthenticationService =
             instance ?: synchronized(this){
                instance ?: TwitterAuthenticationService(firebaseAuth,authenticationHelper)
            }
    }

    /**
     * The view that uses this service has to register itself if it's a [ComponentActivity]
     * or register its container [ComponentActivity] if it's a [Fragment].
     * */
    override fun register(componentActivity: ComponentActivity) {
        this.componentActivity = componentActivity
    }


    /**
     * Main-Safe suspend function that either sends the result of a pending result
     * from a previous login/signup attempt or starts a regular login/signup flow.
     * */
    @Throws(IllegalStateException::class)
    private suspend fun beginAuthentication() : FirebaseUser? {
        val pendingResultTask = firebaseAuth.pendingAuthResult
        var firebaseUser: FirebaseUser?

        if(pendingResultTask != null ){
            // There's already a sign-in/up result, don't go through the authentication flow
            // and retrieve the result
            firebaseUser = getPendingResultTaskResult(pendingResultTask)
        }
        else{
            // Start the normal sign-in/up flow
            firebaseUser = twitterSigningFlow()
        }

        return firebaseUser
    }

    /**
     * Main-Safe suspend function that starts the regular login/signup flow and sends the result through
     * the authentication result observable.
     * */
    @Throws(IllegalStateException::class)
    private suspend fun twitterSigningFlow() : FirebaseUser? {
        if(!this::componentActivity.isInitialized){
            throw IllegalStateException("ComponentActivity isn't registered")
        }
        val pendingResultTask = firebaseAuth
            .startActivityForSignInWithProvider(componentActivity,provider.build())

        return getPendingResultTaskResult(pendingResultTask)
    }

    /**
     * Main-safe function that sends a [FirebaseUser] or an [Exception] to the observer.
     * */
    private suspend fun getPendingResultTaskResult(pendingResultTask: Task<AuthResult>) : FirebaseUser?{
       /*withContext(Dispatchers.IO){
           // TODO currently experimenting with runCatching. Look for all the edge cases surrounding
           // the runCatching and async builders.
            val result = runCatching { pendingResultTask.await() }

           when{
               // TODO make sure to receive the OAuth access token
               // so you can call the Twitter API and get basic profile info by calling their REST API
               result.isSuccess -> authenticationHelper.onSuccess(result.getOrNull()?.user)
               result.isFailure -> authenticationHelper.onFailure(result.exceptionOrNull())
           }

        }*/
        return pendingResultTask
            .await()
            .user
    }

    override suspend fun authenticate(): FirebaseUser? = beginAuthentication()
}