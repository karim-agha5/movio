package com.example.movio.core.interfaces.auth

import androidx.activity.ComponentActivity

interface ComponentActivityRegistrar {
    fun register(componentActivity: ComponentActivity): Unit
}