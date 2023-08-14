package com.example.movio.feature.authentication.services

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import com.example.movio.R
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleSignInService private constructor(
    private val componentActivity: ComponentActivity,
    private val launcher: AuthenticationResultCallbackLauncher
) : LoginServiceContract<LoginCredentials> {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val auth by lazy { Firebase.auth }
    private val tag = this.javaClass.simpleName

    companion object{
        @Volatile private var instance: GoogleSignInService? = null

        fun getInstance(
            componentActivity: ComponentActivity,
            launcher: AuthenticationResultCallbackLauncher
        ): GoogleSignInService =
            instance ?: synchronized(this) {
                instance ?: GoogleSignInService(componentActivity,launcher).also { instance = it }
            }
    }


    init {
        initOneTapClient()
        buildSignInRequest()
    }

    private fun initOneTapClient(){
        oneTapClient = Identity.getSignInClient(componentActivity)
    }

    // Init the One Tap client configurations
    private fun buildSignInRequest(){
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(buildGoogleIdTokenRequestOptions())
            // Signs in the user automatically if there's only 1 credential saved for the app.
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun buildGoogleIdTokenRequestOptions() : GoogleIdTokenRequestOptions{
        return GoogleIdTokenRequestOptions
            .builder()
            .setSupported(true)
            .setServerClientId(componentActivity.getString(R.string.web_client_id))
            // Display the accounts that the user already used in the past.
            .setFilterByAuthorizedAccounts(true)
            .build()
    }


    fun authenticateWithFirebase(data: Intent?){
         try{
             val credential = oneTapClient.getSignInCredentialFromIntent(data)
             val googleIdToken = credential.googleIdToken

             when{
                 // If the user shared a Google account credential
                 // The ID token will be non-null.
                 googleIdToken != null -> {
                     getFirebaseUser(googleIdToken)
                 }
             }

         }catch (e: ApiException){
             AuthenticationHelper.onFailure(e)
         }
    }

    private fun getFirebaseUser(googleIdToken: String?) {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken,null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(componentActivity){
                if(it.isSuccessful){
                    // Signing in is successful
                    val user = auth.currentUser
                    AuthenticationHelper.onSuccess(user)
                }
                else{
                    // Signing in has failed
                    AuthenticationHelper.onFailure(it.exception)
                }
            }
    }


    override suspend fun login(credentials: LoginCredentials?) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener {
                // The Activity Result callback launcher requires an IntentSenderRequest
                // The One Tap client returns an Intent Sender which you use
                // to build an Intent Sender Request and pass it to the launcher
                val intentSenderRequest = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                // TODO add the callback to a continuation coroutine
                launcher.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
            }
            .addOnFailureListener(componentActivity) {
                // The Caller has been temporarily blocked due to too many canceled sign-in prompts
                // or
                // The user doesn't have any saved credentials on the device
                // also deal with generic errors
                AuthenticationHelper.onFailure(it)
            }
    }
}