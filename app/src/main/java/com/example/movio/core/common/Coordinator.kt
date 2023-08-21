package com.example.movio.core.common

interface Coordinator {
    /**
     * Called by the host when the flow starts to display the first screen.
     * */
    fun start()

    /**
     * Handle events sent from the ViewModel.
     * */
    fun onEvent()
}