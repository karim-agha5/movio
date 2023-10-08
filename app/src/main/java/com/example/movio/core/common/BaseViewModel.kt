package com.example.movio.core.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.authentication.signup.Status

/**
 * Experimenting with this class
 * */

// Should also provide status in the base view model --  but it's in an invariant position

abstract class BaseViewModel<D : Data, ActionType : Coordinator.Action, S : Status> : ViewModel() {

    abstract val result: LiveData<S>

    abstract fun postAction(data: D?,action: ActionType)
}


/**
 * Covariant
 * */

/*
abstract class BaseViewModel<out D : Data,out ActionType : Coordinator.Action,out S : Status> : ViewModel() {

    abstract val result: LiveData<@UnsafeVariance S>

    abstract fun postAction(action: @UnsafeVariance ActionType,data: @UnsafeVariance D)
}*/
