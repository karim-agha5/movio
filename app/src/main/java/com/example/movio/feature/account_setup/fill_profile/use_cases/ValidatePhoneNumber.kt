package com.example.movio.feature.account_setup.fill_profile.use_cases

import com.example.movio.feature.common.status.ValidationResultState

class ValidatePhoneNumber {
    fun execute(phoneNumber: String) : ValidationResultState{
        var isValid: ValidationResultState = ValidationResultState.Success

        // TODO implement validation business logic

        return isValid
    }
}