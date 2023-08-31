package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.movio.NavGraphDirections

// TODO consider making this class an inner class to AuthenticationCoordinator
class AuthenticationFlowNavigator(private var _navController: NavController?){

    private val navController get() = _navController!!

    fun navigateToAuthenticationScreen(navOptions: NavOptions?){
        navController.navigate(NavGraphDirections.actionGlobalAuthenticationFragment(),navOptions)
    }

    fun navigateToHomeScreen(navOptions: NavOptions?){
        navController.navigate(NavGraphDirections.actionGlobalHomeFragment(),navOptions)
    }
    fun navigateToEmailAndPasswordScreen(navOptions: NavOptions){

    }
}