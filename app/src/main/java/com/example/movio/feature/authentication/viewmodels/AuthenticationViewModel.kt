package com.example.movio.feature.authentication.viewmodels

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.helpers.Event
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.data_access.IAuthenticationRepository
import com.example.movio.feature.common.models.LoginCredentials
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch


class AuthenticationViewModel(
    application: Application,
    private val authenticationRepository: IAuthenticationRepository
)  : FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>(application,authenticationRepository){

    private val _result: MutableLiveData<Event<SignInStatus>> = MutableLiveData()
    override val result: LiveData<Event<SignInStatus>> = _result

    private val movioContainer                  = getApplication<MovioApplication>().movioContainer
    private var authenticationHelper            = movioContainer.authenticationHelper
    private var firebaseUser: FirebaseUser? = null
    private var disposable: Disposable
    private var isObserverActive = true

    init {
        disposable = authenticationHelper
            .getAuthenticationResultObservableSource()
            .subscribe {
                when(it){
                    is AuthenticationResult.Success -> {
                        if(isObserverActive){
                            onUserReturned(it.user)
                            onSuccessfulAuthentication()
                        }
                    }
                    is AuthenticationResult.Failure -> viewModelScope.launch {
                        if (isObserverActive){ postActionOnFailure(it.throwable) }
                    }
                }
            }

    }

    override fun postActionOnSuccess() = _result.postValue(Event(SignInStatus.EmailVerified))

    override fun postActionOnFailure(throwable: Throwable?) {
        _result.value = Event(SignInStatus.SignInFailed(throwable))
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) =
        when(event) {
            Lifecycle.Event.ON_RESUME   -> isObserverActive = true
            Lifecycle.Event.ON_STOP     -> isObserverActive = false
            else                        -> {/* Do Nothing */}
        }


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
     * interface so it starts the [androidx.activity.result.IntentSenderRequest] and authenticate the user.
     * */
    override fun register(launcher: AuthenticationResultCallbackLauncher) = authenticationRepository.register(launcher)




    override fun register(componentActivity: ComponentActivity) {
        authenticationRepository.register(componentActivity)
        authenticationRepository.register(componentActivity)
    }




    /**
     * The view corresponding to this view model has to unregister itself in the case there are other
     * [AuthenticationResultCallbackLauncher] implementors.
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest]  and authenticate the user.
     * */
    override fun unregister() = authenticationRepository.unregister()


    @Throws(IllegalStateException::class)
    private fun signInWithGoogle(credentials: LoginCredentials?){
        viewModelScope.launch {
            /**
             *  Result in [AuthenticationHelper]
             */
            authenticationRepository.signupWithGoogle()
        }
    }

    @Throws(IllegalStateException::class)
    private fun signInWithTwitter(credentials: LoginCredentials?) =
        viewModelScope.launch {
            /**
             *  Result in [AuthenticationHelper]
             */
            authenticationRepository.signupWithTwitter()
        }

    private fun onUserReturned(firebaseUser: FirebaseUser?){
        this.firebaseUser = firebaseUser
    }
    private fun onSuccessfulAuthentication(){
        authenticateUser(firebaseUser)
        navigateToHome()
    }

    private fun authenticateUser(firebaseUser: FirebaseUser?){
        movioContainer
            .userManager
            .authenticateUser(firebaseUser)
    }

    private fun navigateToHome(){
        // Consider using the lifecycle of the view because config change might happen before navigation
        // May cause unexpected behaviors
        viewModelScope.launch {
            // TODO consider implementing a resource cleaner
            authenticationHelper.disposeAuthenticationResult(disposable)
            coordinator.postAction(AuthenticationActions.ToHomeScreen)
        }
    }

    private fun navigateToSignIn() = viewModelScope.launch { coordinator.postAction(AuthenticationActions.ToSignInScreen) }

    private fun navigateToSignup() = viewModelScope.launch { coordinator.postAction(AuthenticationActions.ToSignupScreen) }
}