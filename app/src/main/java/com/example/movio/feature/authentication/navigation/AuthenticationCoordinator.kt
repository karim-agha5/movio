package com.example.movio.feature.authentication.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.movio.R
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.StateActions
import com.example.movio.core.common.Status
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.FlowState
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class is main-safe as navigation requires to be on the main thread.
 *  It contains all the logic necessary for the navigation in the authentication flow.
 * */
class AuthenticationCoordinator constructor(
    private val flowNavigator: AuthenticationFlowNavigator,
    private val authenticationViewModelsFactory: AuthenticationViewModelsFactory,
    private val flowState: FlowState
    ) : Coordinator {

    //private var currentAction: AuthenticationActions = AuthenticationActions.ToAuthenticationScreen

    /**
     * An action is passed when an event takes place on the UI.
     * Each action represents a destination in the flow.
     * */
    override suspend fun postAction(action: Action) {
        when(action){
            is AuthenticationActions.ToAuthenticationScreen     -> navigateToAuthenticationScreen()
            is AuthenticationActions.ToSignInScreen             -> {
                navigateToSignInScreen()
                //currentAction = AuthenticationActions.ToSignInScreen
            }
            is AuthenticationActions.ToEmailAndPasswordScreen   -> {
                navigateToEmailAndPasswordScreen()
                //currentAction = AuthenticationActions.ToEmailAndPasswordScreen
            }
            is AuthenticationActions.ToHomeScreen               -> navigateToHomeScreen()
            is StateActions.ToAuthenticated                     -> switchState()
        }
    }

    /**
     * Delegates the view model creation to the factory as through the class instance of the view
     * as the coordinator shouldn't be aware of the views.
     *
     * @param  cls instance of the view that requires a view model.
     * @return An instance of [BaseViewModel] based on caller's class type.
     * */
    override fun <D : Data,  A : Action, S : Status>
            requireViewModel(cls: Class<out Fragment>):
            BaseViewModel<D, A, S> {
        // Check the run-time type of the caller and return the appropriate view model accordingly.
        // Check the run-time type of the caller and send it to the factory to return the appropriate
        // view model accordingly.
        // The Coordinator shouldn't be aware of the views. So the appropriate view model creation
        // is delegated to the factory.

        // TODO unsafe cast. work around for the time being. change later.
        return authenticationViewModelsFactory.createViewModel(cls) as BaseViewModel<D, A, S>
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

    private fun buildSignInFragmentNavOptions() : NavOptions{
        return navOptions {
            anim {
                enter = R.anim.from_right_to_current
                exit = R.anim.from_current_to_left
                popEnter = R.anim.from_left_to_current
                popExit = R.anim.from_current_to_right
            }
            popUpTo(R.id.authenticationFragment){
                inclusive = false
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
            popUpTo(R.id.authenticationFragment){
                inclusive = false
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

    private suspend fun navigateToSignInScreen(){
        withContext(Dispatchers.Main){
            flowNavigator.navigateToSignInScreen(buildSignInFragmentNavOptions())
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

    /**
     * Delegates changing the flow state of the app to the current state of the app.
     *
     * (e.g. If the current state of the app is the [AuthenticationFlowState],
     * then the [AuthenticationFlowState] is responsible for changing the state of the [RootCoordinator] to
     * the appropriate state.)
     * */
    private fun switchState(){
        flowState.switchState()
    }
}