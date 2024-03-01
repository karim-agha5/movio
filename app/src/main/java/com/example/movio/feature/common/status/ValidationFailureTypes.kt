package com.example.movio.feature.common.status

// TODO consider moving those hardcoded strings to strings.xml
enum class ValidationFailureTypes(val errorMessage: String  ) {
    EMAIL_FIELD_EMPTY("Please fill the email field"),
    EMAIL_PATTERN_MISMATCH("The email format is incorrect"),
    PASSWORD_FIELD_EMPTY("Please fill the password field"),
    PASSWORD_LENGTH("The password must be 6 characters or more")
}