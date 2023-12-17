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
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.status.SignInStatus
import kotlinx.coroutines.launch
import kotlin.UnsupportedOperationException
import kotlin.jvm.Throws

class SignupViewModel(
    private val emailAndPasswordAuthenticationService: EmailAndPasswordAuthenticationService,
    private val googleSignInService: GoogleSignInService,
    private val twitterAuthenticationService: TwitterAuthenticationService,
    private val authenticationHelper: AuthenticationHelper,
    application: Application
) : BaseViewModel<SignupCredentials, SignupActions, SignInStatus>(application) {

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()

    private val _result = MutableLiveData<SignInStatus>()
    override val result: LiveData<SignInStatus> = _result

    private var exception: Exception? = null


    override fun postAction(data: SignupCredentials?, action: SignupActions) {
        if(action is SignupActions.SignupClicked ){
            signup(data)
        }
    }

    override suspend fun postActionOnSuccess() =
        _result.postValue(SignInStatus.ShouldVerifyEmail)


    override suspend fun postActionOnFailure() {
        //authenticationHelper.onFailure(exception)
    }

    @Throws(UnsupportedOperationException::class)
    override suspend fun onPostResultActionExecuted(action: SignupActions) {
        throw UnsupportedOperationException()
    }


    private fun signup(credentials: SignupCredentials?){
        viewModelScope.launch{
            try{
                emailAndPasswordAuthenticationService.signup(credentials)
                postActionOnSuccess()
            }catch(e: Exception){
                //exception = e
                _result.postValue(SignInStatus.SignInFailed(e))
            }
        }
    }
}
