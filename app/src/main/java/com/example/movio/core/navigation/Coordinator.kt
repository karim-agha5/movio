package com.example.movio.core.navigation

import androidx.fragment.app.Fragment
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.feature.authentication.signup.Status

interface Coordinator {

    /**
     * Handle actions sent from a fragment.
     * A single method to coordinate views in a flow to simplify the Coordinator API used by the views.
     * */
    suspend fun postAction(action: Action)


    /*fun <D: Data, A : Action, S : Status> requireViewModel(cls: Class<out Fragment>) :
            BaseViewModel<D, A, S>*/

    fun <D : Data, A : Action,S : Status> requireViewModel(cls: Class<out Fragment>) : BaseViewModel<D,A,S>


    /**
     * Marker interface to so onEvent() can receive an implementation of the Event interface instead of Any.
     * TODO make sure to replace this and every marker interface, maybe with annotations ????
     * */
    interface Action{}
}

/**
 * Necessary to have this inline extension function so that the client can use it and then the actual
 * implementation will be invoked by the implementors of this interface
 * as you cannot have virtual inline functions
 * */
/*
inline fun <reified D: Data, reified A: Coordinator.Action, reified S : Status>
        Coordinator.requireViewModel(cls: Class<out Fragment>) : BaseViewModel<D, A, S> = requireViewModel(cls)*/

/*
inline fun
        <reified D: Data, reified A: Coordinator.Action>
        Coordinator.requireViewModel(cls: Class<out Fragment>)
        : BaseViewModel<out Data, out Coordinator.Action> = requireViewModel<D,A>(cls)*/
