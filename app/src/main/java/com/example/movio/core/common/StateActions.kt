package com.example.movio.core.common


/**
 * These Actions represent an an event that occurred and supposed to change the state of the coordinator
 * */
sealed class StateActions : Action{
    object ToAuthenticated : StateActions()
}
