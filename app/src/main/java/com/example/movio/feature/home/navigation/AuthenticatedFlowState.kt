package com.example.movio.feature.home.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.FlowContext
import com.example.movio.core.navigation.FlowState

class AuthenticatedFlowState(override val flowContext: FlowContext) : FlowState {

    override fun requireCoordinator(navController: NavController): Coordinator {
        TODO("Not yet implemented")
    }

    override fun <D : Data, A : Action, S : Status> requireViewModel(cls: Class<out Fragment>): BaseViewModel<D, A, S> {
        TODO("Not yet implemented")
    }

    override fun switchState(){
        //flowContext.changeState()
    }
}