package com.example.movio.core.navigation

import androidx.navigation.NavController

interface FlowState {
    val flowContext: FlowContext
    fun requireCoordinator(
        navController: NavController
    ) : Coordinator
    fun switchState()
}