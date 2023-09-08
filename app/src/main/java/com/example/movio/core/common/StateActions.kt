package com.example.movio.core.common

sealed class StateActions : Coordinator.Action{
    object ToAuthenticated : StateActions()
}
