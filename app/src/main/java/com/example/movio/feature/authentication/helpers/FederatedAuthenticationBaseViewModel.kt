package com.example.movio.feature.authentication.helpers

import android.app.Application
import androidx.activity.ComponentActivity
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.core.helpers.Event
import com.example.movio.feature.authentication.services.GoogleSignInService

abstract class FederatedAuthenticationBaseViewModel<D: Data, A: Action, S : Status>
    (application: Application)
    : BaseViewModel<D,A,S>(application){

     open fun register(componentActivity: ComponentActivity) : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

     open fun register(launcher: AuthenticationResultCallbackLauncher) : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

     open fun unregister() : Unit =
        throw UnsupportedOperationException("Override the function in your subclass.")

    abstract fun getGoogleSignInService(): GoogleSignInService
}