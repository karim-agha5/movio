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
class AuthenticationCoordinator(private val flowNavigator: AuthenticationFlowNavigator) : Coordinator {

    /**
     * Starts the authentication flow with the first authentication screen.
     * */
    override fun start() = flowNavigator.start()

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

    suspend fun navigateToHomeFragment() {
        withContext(Dispatchers.Main){
            flowNavigator.navigateToHomeFragment(buildHomeFragmentNavOptions())
        }
    }


    override fun onEvent(/*TODO should receive an argument of type event*/) {
        TODO("Not yet implemented")
    }

}