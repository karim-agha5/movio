package com.example.movio.core.navigation

import com.example.movio.core.navigation.FlowState

interface FlowContext {
    fun changeState(flowState: FlowState)
}