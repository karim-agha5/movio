package com.example.movio.feature.home.navigation

import androidx.navigation.NavController
import com.example.movio.core.common.Coordinator
import com.example.movio.core.common.FlowContext
import com.example.movio.core.common.FlowState

class AuthenticatedFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController): Coordinator {
        TODO("Not yet implemented")
    }

    override fun switchState(){
        //flowContext.changeState()
    }
}