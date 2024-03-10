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

class GoogleSignInService private constructor(
   // private val componentActivity: ComponentActivity,
    // private val launcher: AuthenticationResultCallbackLauncher
) : IFederatedAuthentication,
    AuthenticationResultCallbackLauncherRegistrar{

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var componentActivity: ComponentActivity
    private var launcher: AuthenticationResultCallbackLauncher? = null
    private val auth by lazy { Firebase.auth }

    companion object{
        @Volatile private var instance: GoogleSignInService? = null

        fun getInstance(
            //componentActivity: ComponentActivity,
            //launcher: AuthenticationResultCallbackLauncher
        ): GoogleSignInService =
            instance ?: synchronized(this) {
                instance ?: GoogleSignInService().also { instance = it }
            }
    }

/*
    init {
        initOneTapClient()
        buildSignInRequest()
    }
*/
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

    /**
     * Main-safe
     * */
    @Throws(Exception::class)
    suspend fun authenticateWithFirebase(data: Intent?) : FirebaseUser?{
         /*try{
             val credential = oneTapClient.getSignInCredentialFromIntent(data)
             val googleIdToken = credential.googleIdToken

             when{
                 // If the user shared a Google account credential
                 // The ID token will be non-null.
                 googleIdToken != null ->  getFirebaseUser(googleIdToken)
             }

         }catch (e: ApiException){
             AuthenticationHelper.onFailure(e)
         }*/
        val credential = oneTapClient.getSignInCredentialFromIntent(data)
        val googleIdToken = credential.googleIdToken
        var firebaseUser: FirebaseUser? = null

        when{
            googleIdToken != null -> firebaseUser = getFirebaseUser(googleIdToken)
        }

        return firebaseUser
    }

    /**
     * Main-safe
     * */
    @Throws(Exception::class)
    private suspend fun getFirebaseUser(googleIdToken: String?) : FirebaseUser? {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken,null)
        /*withContext(Dispatchers.IO){
            // TODO a cancellation exception may be thrown from within the async builder. Handle it.
            val deferredResult = async { auth.signInWithCredential(firebaseCredential).await() }
            try {
                val user = deferredResult.await().user
                // Signing in is successful
                AuthenticationHelper.onSuccess(user)
            }catch (e: Exception){
                // Signing in has failed
                AuthenticationHelper.onFailure(e)
            }
        }*/
        return auth
            .signInWithCredential(firebaseCredential)
            .await()
            .user
    }

   /* override suspend fun login(credentials: LoginCredentials?) {
       *//* withContext(Dispatchers.IO) {
            // TODO a cancellation exception may be thrown from within the async builder. Handle it.
            val resultDeferred = async {
                // call the .await() method to wait for the task to be completed
                oneTapClient.beginSignIn(signInRequest).await()
            }

            try {
                // The Activity Result callback launcher requires an IntentSenderRequest
                // The One Tap client returns an Intent Sender which you use
                // to build an Intent Sender Request and pass it to the launcher
                val intentSender = resultDeferred.await().pendingIntent.intentSender
                val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                // TODO add the callback to a continuation coroutine
                *//**//*Log.i("MainActivity", "inside google sign in service | AuthenticationHelper -> ${AuthenticationHelper.hashCode()} \n Observable -> ${AuthenticationHelper.getAuthenticationResultObservableSource().hashCode()}" +
                        "\n is launcher signupfragment ? -> ${launcher is SignupFragment}")*//**//*
                launcher?.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
            } catch (e: Exception) {
                // The Caller has been temporarily blocked due to too many canceled sign-in prompts
                // or
                // The user doesn't have any saved credentials on the device
                // also deal with generic errors
                AuthenticationHelper.onFailure(e)
            }


        }*//*
        val intentSender = oneTapClient
            .beginSignIn(signInRequest)
            .await()
            .pendingIntent
            .intentSender

        launcher?.launchAuthenticationResultCallbackLauncher(IntentSenderRequest.Builder(intentSender).build())
    }
*/
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