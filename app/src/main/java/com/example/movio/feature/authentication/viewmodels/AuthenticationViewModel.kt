package com.example.movio.feature.authentication.viewmodels

import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.signup.status.SignupStatus
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.helpers.UserManager
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import kotlin.jvm.Throws

class AuthenticationViewModel(
    application: Application
)  : FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, SignInStatus>(application){


    override var coordinator = getApplication<MovioApplication>().movioContainer.rootCoordinator.requireCoordinator()

    private val _result: MutableLiveData<SignInStatus> = MutableLiveData()
    override val result: LiveData<SignInStatus> = _result

    private var googleSignInService: GoogleSignInService
    private var twitterAuthenticationService: TwitterAuthenticationService
    private var authenticationHelper = getApplication<MovioApplication>().movioContainer.authenticationHelper
    private var firebaseUser: FirebaseUser? = null
    private var disposable: Disposable

    init {
        disposable = authenticationHelper
            .getAuthenticationResultObservableSource()
            .subscribe {
                when(it){
                    is AuthenticationResult.Success -> {
                        Log.i("MainActivity", "inside init | AuthenticationHelper -> ${authenticationHelper.hashCode()} \n Observable -> ${authenticationHelper.getAuthenticationResultObservableSource().hashCode()}")
                        //navigateToSignInScreen()
                        //navigateToHome()
                        onUserReturned(it.user)
                        onSuccessfulAuthentication()
                    }
                    is AuthenticationResult.Failure -> viewModelScope.launch {
                        postActionOnFailure(it.throwable)
                    }
                }
            }
        googleSignInService             = GoogleSignInService.getInstance()
        twitterAuthenticationService    = TwitterAuthenticationService.getInstance(
            getApplication<MovioApplication>().movioContainer.firebaseAuth,
            authenticationHelper
        )
    }

    override fun postActionOnSuccess() = _result.postValue(SignInStatus.EmailVerified)

    override fun postActionOnFailure(throwable: Throwable?) = _result.postValue(SignInStatus.SignInFailed(throwable))

    override fun onPostResultActionExecuted(action: SignInActions) {
        if(action is SignInActions.SuccessAction){
            onSuccessfulAuthentication()
        }
    }

    override fun postAction(data: LoginCredentials?, action: SignInActions) {
        when(action){
            is SignInActions.GoogleClicked  -> signInWithGoogle(data)
            is SignInActions.TwitterClicked -> signInWithTwitter(data)
            is SignInActions.SignInClicked  -> navigateToSignIn()
            is SignInActions.SignupClicked  -> navigateToSignup()
            else                            -> { /*Do Nothing*/ }
        }
    }


    /**
     * The view corresponding to this view model has to register as a [AuthenticationResultCallbackLauncher].
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest] and authenticate the user.
     * */
    override fun register(launcher: AuthenticationResultCallbackLauncher) = googleSignInService.register(launcher)




    override fun register(componentActivity: ComponentActivity) {
        googleSignInService.register(componentActivity)
        twitterAuthenticationService.register(componentActivity)
    }




    /**
     * The view corresponding to this view model has to unregister itself in the case there are other
     * [AuthenticationResultCallbackLauncher] implementors.
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest] and authenticate the user.
     * */
    override fun unregister() = googleSignInService.unregister()


    override fun getGoogleSignInService() = googleSignInService


    @Throws(IllegalStateException::class)
    private fun signInWithGoogle(credentials: LoginCredentials?){
        googleSignInService.init()
        viewModelScope.launch {
            // Result in [AuthenticationHelper]
            googleSignInService.login(credentials)
        }
    }

    @Throws(IllegalStateException::class)
    private fun signInWithTwitter(credentials: LoginCredentials?) =
        viewModelScope.launch {
            // Result in [AuthenticationHelper]
            twitterAuthenticationService.login(credentials)
        }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        this.firebaseUser = firebaseUser
    }
    private fun onSuccessfulAuthentication(){
        authenticateUser(firebaseUser)
        navigateToHome()
    }

    private fun authenticateUser(firebaseUser: FirebaseUser?){
        val userManager = UserManager.getInstance(getApplication<MovioApplication>().movioContainer.firebaseAuth)
        userManager.authenticateUser(firebaseUser)
    }

    private fun navigateToHome(){
        // Consider using the lifecycle of the view because config change might happen before navigation
        // May cause unexpected behaviors
        viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }

    private fun navigateToSignIn() = viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToSignInScreen) }

    private fun navigateToSignup() = viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToEmailAndPasswordScreen) }
}