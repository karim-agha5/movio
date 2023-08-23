package com.example.movio.core.common

import androidx.navigation.NavController

class AuthenticatedFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController): Coordinator {
        TODO("Not yet implemented")
    }

    override fun switchState(){
        //flowContext.changeState()
    }
}