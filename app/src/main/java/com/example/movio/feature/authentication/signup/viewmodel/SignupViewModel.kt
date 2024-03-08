package com.example.movio.feature.authentication.signup.viewmodel

import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.helpers.CoordinatorDelegate
import com.example.movio.core.helpers.Event
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.common.models.SignupCredentials
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.signup.status.SignupStatus
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.data_access.IAuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.UnsupportedOperationException
import kotlin.jvm.Throws

class SignupViewModel(
    application: Application,
    private val authenticationRepository: IAuthenticationRepository
) : FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>(application,authenticationRepository){

    private val _result = MutableLiveData<Event<SignupStatus>>()
    override val result: LiveData<Event<SignupStatus>> = _result

    private val movioContainer                          = getApplication<MovioApplication>().movioContainer
    /*private val googleSignInService                     = movioContainer.googleSignInService
    private val twitterAuthenticationService            = movioContainer.twitterAuthenticationService
    private val emailAndPasswordAuthenticationService   = movioContainer.emailAndPasswordAuthenticationService*/
    private val authenticationHelper                    = movioContainer.authenticationHelper
    private var disposable: Disposable
    private var isObserverActive = true

    init {
        disposable = authenticationHelper
            .getAuthenticationResultObservableSource()
            .subscribe {
                when(it){
                    is AuthenticationResult.Success -> if(isObserverActive){
                        if(it.user?.isEmailVerified == true){ onUserReturned(it.user) }
                        else                                {
                            Log.i("MainActivity", "viewmodel : ${Thread.currentThread().name}")
                            postActionOnSuccess()
                        }
                    }

                    is AuthenticationResult.Failure -> if(isObserverActive){
                        viewModelScope.launch { postActionOnFailure(it.throwable) }
                    }
                }
            }
    }

    override fun postAction(data: SignupCredentials?, action: SignupActions) {
        when(action){
            is SignupActions.GoogleClicked  -> signupWithGoogle()
            is SignupActions.TwitterClicked -> signupWithTwitter()
            is SignupActions.SignupClicked  -> signup(data)
            is SignupActions.SignInClicked  -> navigateToSignInScreen()
            else                            -> {/*Do Nothing*/}
        }
    }

    override fun postActionOnSuccess() {
        //_result.value = Event(SignupStatus.ShouldVerifyEmail)
        Log.i("MainActivity", "postActionOnSuccess: ${Thread.currentThread().name}")
        _result.postValue(Event(SignupStatus.ShouldVerifyEmail))
    }

    override fun postActionOnFailure(throwable: Throwable?) {
        //_result.value = Event(SignupStatus.SignupFailed(throwable))
        Log.i("MainActivity", "postActionOnFailure: ${Thread.currentThread().name}")
        _result.postValue(Event(SignupStatus.SignupFailed(throwable)))
    }


    @Throws(UnsupportedOperationException::class)
    override fun onPostResultActionExecuted(action: SignupActions) =
        throw UnsupportedOperationException()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) =
        when(event) {
            Lifecycle.Event.ON_RESUME   -> isObserverActive = true
            Lifecycle.Event.ON_STOP     -> isObserverActive = false
            else                        -> {/* Do Nothing */}
        }


    /**
     * The view corresponding to this view model has to register as a [AuthenticationResultCallbackLauncher].
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest] and authenticate the user.
     * */
    override fun register(launcher: AuthenticationResultCallbackLauncher) =
        authenticationRepository.register(launcher)




    override fun register(componentActivity: ComponentActivity) {
        authenticationRepository.register(componentActivity)
        authenticationRepository.register(componentActivity)
    }




    /**
    * The view corresponding to this view model has to unregister itself in the case there are other
    * [AuthenticationResultCallbackLauncher] implementors.
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest] and authenticate the user.
    * */
    override fun unregister() = authenticationRepository.unregister()



    override fun getGoogleSignInService() = authenticationRepository.getGoogleSignInService()


    private fun signup(credentials: SignupCredentials?) =
        viewModelScope.launch{
            try{
                authenticationRepository.signup(credentials)
            }catch(e: Exception){
                Log.i("MainActivity", "Inside catch block")
                postActionOnFailure(e)
            }
        }



    private fun signupWithGoogle(){
        //googleSignInService.init()
        viewModelScope.launch { authenticationRepository.signupWithGoogle() }
    }

    @Throws(IllegalStateException::class)
    private fun signupWithTwitter() = viewModelScope.launch { authenticationRepository.signupWithTwitter() }


    private fun onUserReturned(user: FirebaseUser?){
        authenticateUser(user)
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
        viewModelScope.launch { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }
    private fun navigateToSignInScreen(){
        viewModelScope.launch {
            // TODO consider implementing a resource cleaner 
            authenticationHelper.disposeAuthenticationResult(disposable)
            coordinator.postAction(AuthenticationActions.ToSignInScreen)
        }
    }
}
