package com.example.movio.feature.account_setup.fill_profile.use_cases

import com.example.movio.feature.common.status.ValidationFailureTypes
import com.example.movio.feature.common.status.ValidationResultState

class ValidateFullName {
    fun execute(fullName: String?) : ValidationResultState{
        var isValid: ValidationResultState = ValidationResultState.Success

        if(fullName?.isBlank() == true || fullName?.isEmpty() == true){
            isValid = ValidationResultState.Failure(ValidationFailureTypes.FULL_NAME_FIELD_EMPTY)
        }

        return isValid
    }
}