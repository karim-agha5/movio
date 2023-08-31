package com.example.movio.core.common

import androidx.navigation.NavController
import com.example.movio.feature.authentication.navigation.AuthenticationCoordinator
import com.example.movio.feature.authentication.navigation.AuthenticationFlowNavigator

class AuthenticationFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController) : Coordinator {
        return AuthenticationCoordinator(AuthenticationFlowNavigator(navController),this)
    }

    override fun switchState(){
        flowContext.changeState(AuthenticatedFlowState(flowContext))
    }
}