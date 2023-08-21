package com.example.movio.core.common

interface CoordinatorHost<C : Coordinator> {
    val coordinator: C
}