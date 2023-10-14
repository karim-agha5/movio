package com.example.movio.feature.authentication.signup.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.status.EmailVerificationStatus
import kotlinx.coroutines.launch

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    application: Application
) : BaseViewModel<SignupCredentials, AuthenticationActions, EmailVerificationStatus>(application) {

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<EmailVerificationStatus>()
    override val result: LiveData<EmailVerificationStatus> = _result

    private var exception: Exception? = null


    override fun postAction(data: SignupCredentials?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SignupClicked ){
            signup(data)
        }
    }

    override suspend fun postActionOnSuccess() =
        _result.postValue(EmailVerificationStatus.ShouldVerifyEmail)


    override suspend fun postActionOnFailure() =
        authenticationHelper.onFailure(exception)


    private fun signup(credentials: SignupCredentials?){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                postActionOnSuccess()
            }catch(e: Exception){
                exception = e
            }
        }
    }
}
