package com.example.movio.feature.common.use_cases

import com.example.movio.feature.common.status.ValidationFailureTypes
import com.example.movio.feature.common.status.ValidationResultState

class ValidatePassword {
    fun execute(password: String) : ValidationResultState {
        var state: ValidationResultState = ValidationResultState.Success

        if(password.isBlank()){
            state = ValidationResultState.Failure(ValidationFailureTypes.PASSWORD_FIELD_EMPTY)
        }
        else if(password.length < 6){
            state = ValidationResultState.Failure(ValidationFailureTypes.PASSWORD_LENGTH)
        }

        return state
    }
}