package com.example.movio.feature.home.navigation

import androidx.navigation.NavController
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.FlowContext
import com.example.movio.core.navigation.FlowState

class AuthenticatedFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController): Coordinator {
        TODO("Not yet implemented")
    }

    override fun switchState(){
        //flowContext.changeState()
    }
}