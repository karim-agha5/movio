package com.example.movio.core.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.movio.core.navigation.Coordinator


/*
abstract class BaseViewModel<D : Data, ActionType : Action, S : Status> : ViewModel() {

    abstract val result: LiveData<S>

    abstract fun postAction(data: D?,action: ActionType)
}*/


abstract class BaseViewModel<D : Data, ActionType : Action, S : Status>(application: Application)
    : AndroidViewModel(application) {

    abstract var coordinator: Coordinator

    abstract val result: LiveData<S>

    abstract fun postAction(data: D?,action: ActionType)

    // TODO consider removing these two methods and adding them to an interface(s)
    protected abstract suspend fun postActionOnSuccess()
    protected abstract suspend fun postActionOnFailure(throwable: Throwable?)

    abstract suspend fun onPostResultActionExecuted(action: ActionType);
}