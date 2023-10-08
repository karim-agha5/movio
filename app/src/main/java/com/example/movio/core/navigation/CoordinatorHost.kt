package com.example.movio.core.navigation

interface CoordinatorHost/*<Data,ActionType : Coordinator.Action,Result>*/ {
    //val coordinator: Coordinator<Data,ActionType,Result>
    val coordinator: Coordinator
}