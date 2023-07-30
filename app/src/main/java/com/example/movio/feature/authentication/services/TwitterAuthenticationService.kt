package com.example.movio.feature.authentication.services

import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

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

    private fun authenticate(){
        val pendingResultTask = firebaseAuth.pendingAuthResult
        if(pendingResultTask != null ){
            // There's already a sign-in/up result, don't go through the authentication flow
            // and retrieve the result
            pendingResultTask.addOnSuccessListener {
                authenticationHelper.onSuccess(it.user)
            }.addOnFailureListener {
                authenticationHelper.onFailure(it)
            }
        }
        else{
            // Start the normal sign-in/up flow
            twitterSigningFlow()
        }
    }

    private fun twitterSigningFlow(){
        firebaseAuth
            .startActivityForSignInWithProvider(componentActivity,provider.build())
            .addOnSuccessListener {
                // TODO make sure to receive the OAuth access token
                // so you can call the Twitter API and get basic profile info by calling their REST API
                authenticationHelper.onSuccess(it.user)
            }
            .addOnFailureListener {
                authenticationHelper.onFailure(it)
            }
    }


    override fun login(credentials: LoginCredentials?) {
        authenticate()
    }

    override fun signup(credentials: SignupCredentials?) {
        authenticate()
    }
}