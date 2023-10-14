package com.example.movio.feature.splash.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.helpers.UserManager
import com.example.movio.feature.common.status.UserAuthenticationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Experimenting with delegating the navigation functionality to the view model through injecting the coordinator
 * */
class SplashViewModel(
    private val userManager: UserManager,
    application: Application
    )
    : BaseViewModel<Nothing,AuthenticationActions,UserAuthenticationStatus>(application){

    override var coordinator: Coordinator =
        (application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()


    private val _result: MutableLiveData<UserAuthenticationStatus> = MutableLiveData()
    override val result: LiveData<UserAuthenticationStatus> = _result



    override fun postAction(data: Nothing?, action: AuthenticationActions) {
        if(action is AuthenticationActions.SplashScreenTo){
            viewModelScope.launch(Dispatchers.Main) { navigateFromSplash() }
        }
        else{
            throw IllegalArgumentException()
        }
    }

    override suspend fun postActionOnSuccess() =
        coordinator.postAction(AuthenticationActions.ToHomeScreen)


    override suspend fun postActionOnFailure() =
        coordinator.postAction(AuthenticationActions.ToAuthenticationScreen)


    private suspend fun navigateFromSplash(){
        if(userManager.isLoggedIn()) postActionOnSuccess()
        else                         postActionOnFailure()
    }
}