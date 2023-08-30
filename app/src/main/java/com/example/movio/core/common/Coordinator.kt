package com.example.movio.core.common

interface Coordinator {
    /**
     * Called by the host when the flow starts to display the first screen.
     * */
    //fun start()

    /**
     * Handle actions sent from a fragment.
     * A single method to coordinate views in a flow to simplify the Coordinator API used by the views.
     * */
    suspend fun postAction(action: Action)
    /**
     * Marker interface to so onEvent() can receive an implementation of the Event interface instead of Any.
     * */
    interface Action{}
}