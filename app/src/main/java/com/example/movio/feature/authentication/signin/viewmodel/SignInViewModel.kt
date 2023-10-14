package com.example.movio.feature.authentication.signin.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.status.EmailVerificationStatus
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    private val application: Application
) : BaseViewModel<LoginCredentials, AuthenticationActions, EmailVerificationStatus>(application) {

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result

    private var firebaseUser: FirebaseUser? = null

    override fun postAction(data: LoginCredentials?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SignInClicked){
            login(data)
        }
    }

    override suspend fun postActionOnSuccess() {
        _result.postValue(EmailVerificationStatus.EmailVerified)
        onSuccessfulAuthentication(firebaseUser)
    }

    override suspend fun postActionOnFailure() {
        _result.postValue(EmailVerificationStatus.EmailNotVerified)
    }

    private fun login(credentials: LoginCredentials?){
        viewModelScope.launch {
            try{
                val user = emailAndPasswordAuthenticationService.login(credentials)
                onUserReturned(user)
            }catch(e: Exception){
                authenticationHelper.onFailure(e)
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