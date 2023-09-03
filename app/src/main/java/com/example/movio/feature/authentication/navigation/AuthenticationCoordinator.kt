package com.example.movio.feature.authentication.navigation

import android.util.Log
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.movio.R
import com.example.movio.core.common.Coordinator
import com.example.movio.core.common.FlowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class is main-safe as navigation requires to be on the main thread.
 *  It contains all the logic necessary for the navigation in the authentication flow.
 * */
class AuthenticationCoordinator constructor(
    private val flowNavigator: AuthenticationFlowNavigator,
    private val flowState: FlowState
    ) : Coordinator {

    /**
     * An action is passed when an event takes place on the UI.
     * Each action represents a destination in the flow.
     * */
    override suspend fun postAction(action: Coordinator.Action) {
        when(action){
            is AuthenticationActions.ToAuthenticationScreen -> navigateToAuthenticationScreen()
            is AuthenticationActions.ToHomeScreen -> {
                navigateToHomeScreen()
                flowState.switchState()
            }
            is AuthenticationActions.ToEmailAndPasswordScreen -> navigateToEmailAndPasswordScreen()
        }
    }

    private fun buildAuthenticationFragmentNavOptions() : NavOptions {
        return navOptions {
            anim {
                enter = android.R.anim.fade_in
                popExit = android.R.anim.fade_out
            }
            popUpTo(R.id.splashFragment){
                inclusive = true
            }
        }
    }

    private fun buildEmailAndPasswordSignupNavOptions() : NavOptions{
        return navOptions {
            anim {
                enter = R.anim.from_right_to_current
                exit = R.anim.from_current_to_left
                popEnter = R.anim.from_left_to_current
                popExit = R.anim.from_current_to_right
            }
        }
    }

    private fun buildHomeFragmentNavOptions() : NavOptions {
        return navOptions {
            anim {
                enter = R.anim.from_right_to_current
                exit = R.anim.from_current_to_left
            }
            popUpTo(R.id.nav_graph){
                inclusive = false
            }
        }
    }

    private suspend fun navigateToAuthenticationScreen(){
        withContext(Dispatchers.Main){
            flowNavigator.navigateToAuthenticationScreen(buildAuthenticationFragmentNavOptions())
        }
    }

    private suspend fun navigateToEmailAndPasswordScreen(){
        withContext(Dispatchers.Main){
             flowNavigator.navigateToEmailAndPasswordScreen(buildEmailAndPasswordSignupNavOptions())
        }
    }

    private suspend fun navigateToHomeScreen() {
        withContext(Dispatchers.Main){
            flowNavigator.navigateToHomeScreen(buildHomeFragmentNavOptions())
        }
    }
}