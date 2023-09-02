package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavController
import com.example.movio.feature.home.navigation.AuthenticatedFlowState
import com.example.movio.core.common.Coordinator
import com.example.movio.core.common.FlowContext
import com.example.movio.core.common.FlowState

class AuthenticationFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController) : Coordinator {
        return AuthenticationCoordinator(AuthenticationFlowNavigator(navController),this)
    }

    override fun switchState(){
        flowContext.changeState(AuthenticatedFlowState(flowContext))
    }
}