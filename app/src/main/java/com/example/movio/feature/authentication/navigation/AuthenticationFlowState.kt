package com.example.movio.feature.authentication.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.feature.home.navigation.AuthenticatedFlowState
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.FlowContext
import com.example.movio.core.navigation.FlowState
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory

/**
 * Represents the authentication navigation flow state of the app.
 *
 * Responsible for providing the appropriate coordinator of the authentication navigation flow and changing
 * the [RootCoordinator] state to the appropriate state when the authentication navigation flow is done.
 * */
class AuthenticationFlowState(
    override val flowContext: FlowContext,
    private val authenticationViewModelsFactory: AuthenticationViewModelsFactory
    ) : FlowState {

    /**
     * Provides the appropriate [Coordinator] implementation associated with the authentication navigation flow
     * */
    // TODO figure out a way to return the same coordinator without memory leak
    override fun requireCoordinator(navController: NavController) : Coordinator {
        return AuthenticationCoordinator(
            AuthenticationFlowNavigator(navController),
            authenticationViewModelsFactory,
            this
        )
    }

    override fun <D : Data,  A : Action, S : Status>
            requireViewModel(cls: Class<out Fragment>): BaseViewModel<D,A,S> {
        return authenticationViewModelsFactory.createViewModel(cls) as BaseViewModel<D, A, S>
    }

    /**
     * Changes the state of the [RootCoordinator] to the appropriate state.
     * */
    override fun switchState(){
        flowContext.changeState(AuthenticatedFlowState(flowContext))
    }
}