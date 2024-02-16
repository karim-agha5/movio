package com.example.movio.feature.common.use_cases

import com.example.movio.core.util.FormUtils
import com.example.movio.feature.common.status.ValidationFailureTypes
import com.example.movio.feature.common.status.ValidationResultState

class ValidateEmail {
    fun execute(email: String) : ValidationResultState {
        var valid: ValidationResultState = ValidationResultState.Success

        if(email.isBlank()) {
            valid = ValidationResultState.Failure(ValidationFailureTypes.EMAIL_FIELD_EMPTY)
        }
        else if(!FormUtils.emailMatches(email)) {
            valid = ValidationResultState.Failure(ValidationFailureTypes.EMAIL_PATTERN_MISMATCH)
        }

        return valid
    }
}