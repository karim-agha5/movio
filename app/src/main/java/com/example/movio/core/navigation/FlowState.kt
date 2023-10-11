package com.example.movio.core.navigation

import androidx.navigation.NavController

/**
 * A contract for any implementation of any of the app navigation flow state.
 *
 * Each navigation flow state should be able to provide the [FlowContext]
 * with the appropriate implementation of the [Coordinator]
 * and to switch the current navigation flow state of the [FlowContext].
 * */
interface FlowState {
    val flowContext: FlowContext
    fun requireCoordinator(navController: NavController) : Coordinator
    fun switchState()
}