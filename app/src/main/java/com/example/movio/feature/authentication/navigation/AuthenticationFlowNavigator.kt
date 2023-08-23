package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.movio.NavGraphDirections

// TODO consider making this class an inner class to AuthenticationCoordinator
class AuthenticationFlowNavigator(private val navController: NavController) {

    /**
     * Starts the authentication flow with the first authentication screen.
     * */
    fun start(){
        navController.navigate(NavGraphDirections.actionGlobalAuthenticationFragment())
    }

    fun navigateToHomeScreen(navOptions: NavOptions?){
        navController.navigate(
            NavGraphDirections.actionGlobalHomeFragment(),navOptions
        )
    }

    fun navigateToEmailAndPasswordScreen(navOptions: NavOptions){

    }
}