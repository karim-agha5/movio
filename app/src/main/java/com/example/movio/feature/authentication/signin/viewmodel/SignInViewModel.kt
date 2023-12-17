package com.example.movio.feature.authentication.signin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val googleSignInService: GoogleSignInService,
    private val twitterAuthenticationService: TwitterAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    private val application: Application
) : BaseViewModel<LoginCredentials, SignInActions, SignInStatus>(application) {

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<SignInStatus>()
    override val result: LiveData<SignInStatus> = _result

    private var firebaseUser: FirebaseUser? = null

    override fun postAction(data: LoginCredentials?, action: SignInActions) {
        when(action){
            is SignInActions.SignInClicked -> login(data)
            is SignInActions.FacebookClicked -> {/* Do Nothing */}
            is SignInActions.GoogleClicked -> signInWithGoogle(data)
            else -> signInWithTwitter(data)
        }
    }

    override suspend fun postActionOnSuccess() {
        _result.postValue(SignInStatus.EmailVerified)
    }

    override suspend fun postActionOnFailure() {
        _result.postValue(SignInStatus.EmailNotVerified)
    }

    override suspend fun onPostResultActionExecuted(action: SignInActions) {
        if(action is SignInActions.SuccessAction){
            onSuccessfulAuthentication(firebaseUser)
        }
    }

    private fun login(credentials: LoginCredentials?){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                //authenticationHelper.onFailure(e)
                _result.postValue(SignInStatus.SignInFailed(e))
            }
        }
    }

    private fun signInWithGoogle(credentials: LoginCredentials?){
        viewModelScope.launch {
            // Result in [AuthenticationHelper]
            googleSignInService.login(credentials)

            authenticationHelper
                .getAuthenticationResultObservableSource()
                .subscribe {
                    when(it){
                        is AuthenticationResult.Success -> viewModelScope.launch { onUserReturned(it.user) }
                        is AuthenticationResult.Failure -> _result.postValue(SignInStatus.SignInFailed(it.throwable))
                    }
                }
        }
    }

    private fun signInWithTwitter(credentials: LoginCredentials?){
        viewModelScope.launch {
            // Result in [AuthenticationHelper]
            twitterAuthenticationService.login(credentials)

            authenticationHelper
                .getAuthenticationResultObservableSource()
                .subscribe {
                    when(it){
                        is AuthenticationResult.Success -> viewModelScope.launch { onUserReturned(it.user) }
                        is AuthenticationResult.Failure -> _result.postValue(SignInStatus.SignInFailed(it.throwable))
                    }
                }
        }
    }

    private suspend fun onUserReturned(firebaseUser: FirebaseUser?){
        if(firebaseUser?.isEmailVerified == true) {
            postActionOnSuccess()
        }
        else{
            postActionOnFailure()
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
}