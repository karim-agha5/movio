package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavController
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
    override fun requireCoordinator(navController: NavController) : Coordinator {
        return AuthenticationCoordinator(
            AuthenticationFlowNavigator(navController),
            authenticationViewModelsFactory,
            this
        )
    }

    /**
     * Changes the state of the [RootCoordinator] to the appropriate state.
     * */
    override fun switchState(){
        flowContext.changeState(AuthenticatedFlowState(flowContext))
    }
}