package com.example.movio.feature.authentication.services

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import com.example.movio.R
import com.example.movio.core.common.IFederatedAuthentication
import com.example.movio.core.interfaces.auth.AuthenticationResultCallbackLauncherRegistrar
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException
import kotlin.jvm.Throws

class GoogleSignInService private constructor()
    : IFederatedAuthentication,
    AuthenticationResultCallbackLauncherRegistrar{

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var componentActivity: ComponentActivity
    private var launcher: AuthenticationResultCallbackLauncher? = null
    private val auth by lazy { Firebase.auth }

    companion object{
        @Volatile private var instance: GoogleSignInService? = null

        fun getInstance(
        ): GoogleSignInService =
            instance ?: synchronized(this) {
                instance ?: GoogleSignInService().also { instance = it }
            }
    }

    override fun register(launcher: AuthenticationResultCallbackLauncher) {
        this.launcher = launcher
    }

    override fun unregister() {
        this.launcher = null
    }

    override fun register(componentActivity: ComponentActivity) {
        this.componentActivity = componentActivity
    }

    @Throws(IllegalStateException::class)
    fun init(){
        if(launcher == null || !this::componentActivity.isInitialized){
            throw IllegalStateException("AuthenticationResultCallbackLauncher or ComponentActivity isn't registered")
        }
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

    // Used for building the one tap client configurations
    private fun buildGoogleIdTokenRequestOptions() : GoogleIdTokenRequestOptions{
        return GoogleIdTokenRequestOptions
            .builder()
            .setSupported(true)
            .setServerClientId(componentActivity.getString(R.string.web_client_id))
            // Display the accounts that the user already used in the past.
            .setFilterByAuthorizedAccounts(true)
            .build()
    }


    @Throws(Exception::class)
    suspend fun authenticateWithFirebase(data: Intent?) : FirebaseUser?{
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val googleIdToken = credential.googleIdToken
        var firebaseUser: FirebaseUser? = null

        when{
            googleIdToken != null -> firebaseUser = getFirebaseUser(googleIdToken)
        }

        return firebaseUser
    }

    @Throws(Exception::class)
    private suspend fun getFirebaseUser(googleIdToken: String?) : FirebaseUser? {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken,null)
        return auth
            .signInWithCredential(firebaseCredential)
            .await()
            .user
    }

    override suspend fun authenticate(): FirebaseUser? {
        val intentSender = oneTapClient
            .beginSignIn(signInRequest)
            .await()
            .pendingIntent
            .intentSender

        launcher?.launchAuthenticationResultCallbackLauncher(IntentSenderRequest.Builder(intentSender).build())

       return null
    }
}