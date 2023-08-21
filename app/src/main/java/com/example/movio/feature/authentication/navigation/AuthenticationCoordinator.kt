package com.example.movio.feature.authentication.navigation

import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.movio.R
import com.example.movio.core.common.Coordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class is main-safe as navigation requires to be on the main thread.
 *  It contains all the logic necessary for the navigation in the authentication flow.
 * */
class AuthenticationCoordinator private constructor(private val flowNavigator: AuthenticationFlowNavigator)
    : Coordinator {


    companion object{
        @Volatile private var instance: AuthenticationCoordinator? = null

        fun getInstance(flowNavigator: AuthenticationFlowNavigator) =
             instance ?: synchronized(this){
                instance ?: AuthenticationCoordinator(flowNavigator).also { instance = it }
        }
    }

    /**
     * An action is passed when an event takes place on the UI.
     * Each action represents a destination in the flow.
     * */
    override suspend fun postAction(action: Coordinator.Action) {
        when(action){
            is AuthenticationActions.ToHomeScreen -> navigateToHomeScreen()
            is AuthenticationActions.ToEmailAndPasswordScreen -> navigateToEmailAndPasswordScreen()
        }
    }

    private fun buildHomeFragmentNavOptions() : NavOptions {
        return navOptions {
            anim {
                enter = R.anim.from_right_to_current
                exit = R.anim.from_current_to_left
            }
            popUpTo(R.id.authenticationFragment){
                inclusive = true
            }
        }
    }

    private suspend fun navigateToHomeScreen() {
        withContext(Dispatchers.Main){
            flowNavigator.navigateToHomeScreen(buildHomeFragmentNavOptions())
        }
    }

    private suspend fun navigateToEmailAndPasswordScreen(){
        withContext(Dispatchers.Main){
            // flowNavigator.navigateToEmailAndPasswordScreen()
        }
    }

}