package com.example.movio.feature.common.status

sealed class ValidationResultState {
    data object Neutral : ValidationResultState()
    data object Success : ValidationResultState()

    data class Failure(val type: ValidationFailureTypes) : ValidationResultState()
}