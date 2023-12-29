package com.example.movio.feature.authentication.signin.viewmodel

import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.StateActions
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import kotlin.jvm.Throws

class SignInViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    //private val googleSignInService: GoogleSignInService,
    //private val twitterAuthenticationService: TwitterAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    private val application: Application
) : FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, SignInStatus>(application) {

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<SignInStatus>()
    override val result: LiveData<SignInStatus> = _result

    private var firebaseUser: FirebaseUser? = null

    private var googleSignInService: GoogleSignInService
    private var twitterAuthenticationService: TwitterAuthenticationService
    private var disposable: Disposable


    init {
        disposable = authenticationHelper
            .getAuthenticationResultObservableSource()
            .subscribe {
                when(it){
                    is AuthenticationResult.Success -> viewModelScope.launch { onUserReturned(it.user) }
                    is AuthenticationResult.Failure -> _result.postValue(SignInStatus.SignInFailed(it.throwable))
                }
            }
        googleSignInService             = GoogleSignInService.getInstance()
        twitterAuthenticationService    = TwitterAuthenticationService.getInstance(
            getApplication<MovioApplication>().movioContainer.firebaseAuth,
            authenticationHelper
        )
    }


    override fun postAction(data: LoginCredentials?, action: SignInActions) {
        when(action){
            is SignInActions.SignInClicked -> login(data)
            is SignInActions.FacebookClicked -> {/* Do Nothing */}
            is SignInActions.GoogleClicked -> signInWithGoogle(data)
            else -> signInWithTwitter(data)
        }
    }

    override fun postActionOnSuccess() = _result.postValue(SignInStatus.EmailVerified)


    override fun postActionOnFailure(throwable: Throwable?) = _result.postValue(SignInStatus.EmailNotVerified)


    override fun onPostResultActionExecuted(action: SignInActions) {
        if(action is SignInActions.SuccessAction){
            onSuccessfulAuthentication(firebaseUser)
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


    private fun login(credentials: LoginCredentials?) =
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                //authenticationHelper.onFailure(e)
                _result.postValue(SignInStatus.SignInFailed(e))
            }
        }


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
        val providerId = firebaseUser?.providerData?.get(firebaseUser.providerData.size - 1)?.providerId
        if(providerId?.equals("password") == true){
            if(firebaseUser.isEmailVerified) {
                postActionOnSuccess()
            }
            else{
                postActionOnFailure(null)
            }
        }
        else{
            postActionOnSuccess()
        }
    }

    private fun onSuccessfulAuthentication(firebaseUser: FirebaseUser?){
        authenticateUser(firebaseUser)
        navigateToHome()
    }

    private fun authenticateUser(firebaseUser: FirebaseUser?){
        val userManager = UserManager.getInstance((application as MovioApplication).movioContainer.firebaseAuth)
        userManager.authenticateUser(firebaseUser)
    }

    private fun navigateToHome(){
        // Consider using the lifecycle of the view because config change might happen before navigation
        // May cause unexpected behaviors
        viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }

    override fun onCleared() {
        super.onCleared()
        //disposable.dispose()
    }
}