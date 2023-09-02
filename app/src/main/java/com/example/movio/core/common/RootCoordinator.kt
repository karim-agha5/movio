package com.example.movio.core.common

import androidx.navigation.NavController
import com.example.movio.feature.authentication.navigation.AuthenticationFlowState

class RootCoordinator : FlowContext{

    /**
     * Each coordinator object has a nullable and non-nullable reference in order to dispose the object
     * when its flow is completed and to avoid continuously checking for nullability.
     * */
    private var _navController: NavController? = null
    private val navController get() = _navController!!
    private lateinit var state: FlowState

    /**
     * Must be called when the MainActivity's onCreate is called.
     * */
    fun init(navController: NavController){
        _navController = navController
        initState()
    }

    private fun initState(){
        // TODO add logic to determine the state of the coordinator
        // Should the flow starts as logged/signed before
        //, should the flow start from authentication
        // , or should the flor start as if the app is newly installed.
        state = AuthenticationFlowState(this)
    }

    fun requireCoordinator() : Coordinator{
        if(_navController == null){/*TODO throw a custom exception*/}

        // The current state is responsible for returning the appropriate coordinator
        return state.requireCoordinator(navController)
    }

    override fun changeState(flowState: FlowState) {
        state = flowState
    }
}