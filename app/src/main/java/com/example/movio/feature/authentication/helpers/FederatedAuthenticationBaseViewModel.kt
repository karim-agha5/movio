package com.example.movio.feature.authentication.helpers

import android.app.Application
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.core.helpers.CoordinatorDelegate
import com.example.movio.core.helpers.Event
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.common.data_access.IAuthenticationRepository
import kotlinx.coroutines.launch

abstract class FederatedAuthenticationBaseViewModel<D: Data, A: Action, S : Status>(
    application: Application,
    private val authenticationRepository: IAuthenticationRepository
) : BaseViewModel<D,A,S>(application), LifecycleEventObserver {

    override val coordinator: Coordinator by CoordinatorDelegate(application as MovioApplication)

     open fun register(componentActivity: ComponentActivity) : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

     open fun register(launcher: AuthenticationResultCallbackLauncher) : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

     open fun unregister() : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

    fun authenticateWithFirebase(data: Intent?) = viewModelScope.launch {
        authenticationRepository.authenticateWithFirebase(data)
    }

    abstract fun getGoogleSignInService(): GoogleSignInService
}