package com.example.movio.feature.authentication.signup.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationResult
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
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val googleSignInService: GoogleSignInService,
    private val twitterAuthenticationService: TwitterAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    application: Application
) : BaseViewModel<SignupCredentials, SignupActions, SignupStatus>(application) {


    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<SignupStatus>()
    override val result: LiveData<SignupStatus> = _result

    private var disposable: Disposable

    init {
        disposable = authenticationHelper
            .getAuthenticationResultObservableSource()
            .subscribe {
                when(it){
                    is AuthenticationResult.Success -> navigateToSignInScreen()
                    is AuthenticationResult.Failure -> viewModelScope.launch { postActionOnFailure(it.throwable) }
                }
            }
    }

    override fun postAction(data: SignupCredentials?, action: SignupActions) {
        when(action){
            is SignupActions.GoogleClicked  -> signupWithGoogle()
            is SignupActions.TwitterClicked -> signupWithTwitter()
            is SignupActions.SignupClicked  -> signup(data)
            else                            -> navigateToSignInScreen()
        }
    }

    override fun postActionOnSuccess() = _result.postValue(SignupStatus.ShouldVerifyEmail)

    override fun postActionOnFailure(throwable: Throwable?) = _result.postValue(SignupStatus.SignupFailed(throwable))

    @Throws(UnsupportedOperationException::class)
    override suspend fun onPostResultActionExecuted(action: SignupActions) {
        throw UnsupportedOperationException()
    }

    private fun signup(credentials: SignupCredentials?){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                postActionOnSuccess()
            }catch(e: Exception){ postActionOnFailure(e) }
        }
    }


    private fun signupWithGoogle(){
        viewModelScope.launch {
            googleSignInService.login(null)
            authenticationHelper
                .getAuthenticationResultObservableSource()
                .subscribe {
                    when(it){
                        is AuthenticationResult.Success -> onUserReturned(it.user)
                        is AuthenticationResult.Failure -> postActionOnFailure(it.throwable)
                    }
                }
        }
    }

    private fun signupWithTwitter(){
        viewModelScope.launch {
            twitterAuthenticationService.signup(null)
            authenticationHelper
                .getAuthenticationResultObservableSource()
                .subscribe {
                    when(it){
                        is AuthenticationResult.Success -> onUserReturned(it.user)
                        is AuthenticationResult.Failure -> postActionOnFailure(it.throwable)
                    }
                }
        }
    }

    private fun onUserReturned(user: FirebaseUser?){
        authenticateUser(user)
        navigateToHome()
    }

    private fun authenticateUser(user: FirebaseUser?){
        getApplication<MovioApplication>()
            .movioContainer
            .userManager
            .authenticateUser(user)
    }

    private fun navigateToHome(){
        // Consider using the lifecycle of the view because config change might happen before navigation
        // May cause unexpected behaviors
        viewModelScope.launch(Dispatchers.Main) { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }
    private fun navigateToSignInScreen(){
        viewModelScope.launch { coordinator.postAction(AuthenticationActions.ToSignInScreen) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
