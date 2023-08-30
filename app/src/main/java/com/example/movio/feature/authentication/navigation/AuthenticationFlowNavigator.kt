package com.example.movio.feature.authentication.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.movio.NavGraphDirections

// TODO consider making this class an inner class to AuthenticationCoordinator
class AuthenticationFlowNavigator(private var navController: NavController) {

    /**
     * Starts the authentication flow with the first authentication screen.
     * */
    fun start(){
        navController.navigate(NavGraphDirections.actionGlobalAuthenticationFragment())
    }

    fun navigateToAuthenticationScreen(navOptions: NavOptions?){
        navController.navigate(NavGraphDirections.actionGlobalAuthenticationFragment(),navOptions)
    }

    fun navigateToHomeScreen(navOptions: NavOptions?){
        navController.navigate(NavGraphDirections.actionGlobalHomeFragment(),navOptions)
    }
    fun navigateToEmailAndPasswordScreen(navOptions: NavOptions){

    }
}