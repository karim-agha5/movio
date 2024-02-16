package com.example.movio.feature.common.status

enum class ValidationFailureTypes(val errorMessage: String) {
    EMAIL_FIELD_EMPTY("Please fill the email field"),
    EMAIL_PATTERN_MISMATCH("The email format is incorrect")
}