package com.example.movio.feature.authentication.services

import android.util.Log
import androidx.activity.ComponentActivity
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult


/**
 * This class still requires registering an Activity Result callback in order to pass the data received
 * in the callback to the CallbackManager to receive the user's information in the LoginManager callback.
 *
 * This class doesn't login nor sign-up as Meta now requires all Android apps to be published on Google Play Store
 * to use Facebook's login SDK. This is why the Activity Result callback isn't registered yet.
 *
 * // TODO continue implementing this class when the app is published on Google Play Store.
 *
 * */

class FacebookAuthenticationService private constructor(
    private val componentActivity: ComponentActivity
) : LoginServiceContract<LoginCredentials>{

    private val callbackManager = CallbackManager.Factory.create()
    private val loginManager = LoginManager.getInstance()
    private val permissions = listOf("email","public_profile")


    companion object{
        @Volatile
        private var instance: FacebookAuthenticationService? = null

        fun getInstance(componentActivity: ComponentActivity) : FacebookAuthenticationService{
            return instance ?: synchronized(this){
                instance ?: FacebookAuthenticationService(componentActivity).also { instance = it }
            }
        }
    }


    init {
        setupCallbacks()
    }

    private fun setupCallbacks(){
        loginManager.registerCallback(callbackManager, object:
            FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.i("Exception", "Canceled")
            }

            override fun onError(error: FacebookException) {
                Log.i("Exception", "error | ${error.cause}\n${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                Log.i("Exception", "${result.accessToken}")
            }

        })
    }

    override suspend fun login(credentials: LoginCredentials?) {
        loginManager.logIn(componentActivity,callbackManager,permissions)
    }
}