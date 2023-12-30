package com.example.movio.feature.authentication.signup.viewmodel

import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.signup.status.SignupStatus
import com.example.movio.feature.common.actions.AuthenticationActions
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.UnsupportedOperationException
import kotlin.jvm.Throws

class SignupViewModel(
    //private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    //private val googleSignInService: GoogleSignInService,
    //private val twitterAuthenticationService: TwitterAuthenticationService,
    //private val authenticationHelper: AuthenticationHelper,
    application: Application
) : FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, SignupStatus>(application){


    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<SignupStatus>()
    override val result: LiveData<SignupStatus> = _result

    private val movioContainer                          = getApplication<MovioApplication>().movioContainer
    private val googleSignInService                     = movioContainer.googleSignInService
    private val twitterAuthenticationService            = movioContainer.twitterAuthenticationService
    private val emailAndPasswordAuthenticationService   = movioContainer.emailAndPasswordAuthenticationService
    private val authenticationHelper                    = movioContainer.authenticationHelper
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
                    }
                    is AuthenticationResult.Failure -> viewModelScope.launch {
                        postActionOnFailure(it.throwable)
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

    override fun postActionOnSuccess() = _result.postValue(SignupStatus.ShouldVerifyEmail)

    override fun postActionOnFailure(throwable: Throwable?) {
        _result.value = SignupStatus.SignupFailed(throwable)
    }



    @Throws(UnsupportedOperationException::class)
    override fun onPostResultActionExecuted(action: SignupActions) =
        throw UnsupportedOperationException()


    /**
     * The view corresponding to this view model has to register as a [AuthenticationResultCallbackLauncher].
     * Each view that uses [GoogleSignInService] has to implement the [AuthenticationResultCallbackLauncher]
     * interface so it starts the [IntentSenderRequest] and authenticate the user.
     * */
    override fun register(launcher: AuthenticationResultCallbackLauncher) =
        googleSignInService.register(launcher)




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


    private fun signup(credentials: SignupCredentials?) =
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                postActionOnSuccess()
            }catch(e: Exception){ postActionOnFailure(e) }
        }



    private fun signupWithGoogle(){
        googleSignInService.init()
        viewModelScope.launch { googleSignInService.login(null) }
    }

    @Throws(IllegalStateException::class)
    private fun signupWithTwitter() = viewModelScope.launch { twitterAuthenticationService.signup(null) }


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
        viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }
    private fun navigateToSignInScreen(){
        Log.i("MainActivity", "navigateToSignInScreen should be executed ")
        viewModelScope.launch { coordinator.postAction(AuthenticationActions.ToSignInScreen) }
    }

    override fun onCleared() {
        super.onCleared()
        //disposable.dispose()
    }
}
