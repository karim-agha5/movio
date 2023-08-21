package com.example.movio.core.common

import androidx.navigation.NavController
import com.example.movio.feature.authentication.navigation.AuthenticationCoordinator
import com.example.movio.feature.authentication.navigation.AuthenticationFlowNavigator

class RootCoordinator {

    private lateinit var navController: NavController
    private lateinit var authenticationCoordinator: AuthenticationCoordinator

    /**
     * Must be called when onCreate is called.
     * */
    fun init(navController: NavController){
        if(!this::navController.isInitialized) {
            this.navController = navController
            initCoordinators()
        }
    }

    private fun initCoordinators(){
        authenticationCoordinator =
            AuthenticationCoordinator.getInstance(AuthenticationFlowNavigator(navController))
    }

    fun requireCoordinator() : Coordinator{
        if(!this::navController.isInitialized){/*TODO throw a custom exception*/}
        // TODO return the appropriate coordinator based on the state
        return authenticationCoordinator
    }
}