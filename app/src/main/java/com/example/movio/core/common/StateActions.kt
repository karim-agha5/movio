package com.example.movio.core.common

import com.example.movio.core.navigation.Coordinator

sealed class StateActions : Coordinator.Action{
    object ToAuthenticated : StateActions()
}
