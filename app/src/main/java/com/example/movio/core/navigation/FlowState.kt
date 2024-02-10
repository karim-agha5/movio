package com.example.movio.core.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory

/**
 * A contract for any implementation of any of the app navigation flow state.
 *
 * Each navigation flow state should be able to provide the [FlowContext]
 * with the appropriate implementation of the [Coordinator]
 * and to switch the current navigation flow state of the [FlowContext].
 * */
interface FlowState {
    val flowContext: FlowContext
    fun requireCoordinator(navController: NavController) : Coordinator

    fun <D : Data,  A : Action, S : Status> requireViewModel(cls: Class<out Fragment>) : BaseViewModel<D,A,S>

    fun switchState()
}