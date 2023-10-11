package com.example.movio.core.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


abstract class BaseViewModel<D : Data, ActionType : Action, S : Status> : ViewModel() {

    abstract val result: LiveData<S>

    abstract fun postAction(data: D?,action: ActionType)
}