package com.example.movio.core.common

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.movio.core.interfaces.auth.AuthenticationResultCallbackLauncherRegistrar
import com.example.movio.core.interfaces.auth.ComponentActivityRegistrar
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher

abstract class BaseViewModel<D : Data, ActionType : Action, S : Status>(application: Application)
    : AndroidViewModel(application){

    abstract var coordinator: Coordinator

    abstract val result: LiveData<S>

    abstract fun postAction(data: D?,action: ActionType)

    // TODO consider removing these two methods and adding them to an interface(s)
    protected abstract fun postActionOnSuccess()
    protected abstract fun postActionOnFailure(throwable: Throwable?)
    abstract fun onPostResultActionExecuted(action: ActionType)


}