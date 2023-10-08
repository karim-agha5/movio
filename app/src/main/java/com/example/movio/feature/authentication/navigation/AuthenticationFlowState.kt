package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavController
import com.example.movio.core.helpers.ViewModelsFactoryProvider
import com.example.movio.feature.home.navigation.AuthenticatedFlowState
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.FlowContext
import com.example.movio.core.navigation.FlowState
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory

class AuthenticationFlowState(
    override val flowContext: FlowContext,
    private val authenticationViewModelsFactory: AuthenticationViewModelsFactory
    ) : FlowState {

    override fun requireCoordinator(navController: NavController) : Coordinator {
        return AuthenticationCoordinator(
            AuthenticationFlowNavigator(navController),
            authenticationViewModelsFactory,
            this
        )
    }

    override fun switchState(){
        flowContext.changeState(AuthenticatedFlowState(flowContext))
    }
}