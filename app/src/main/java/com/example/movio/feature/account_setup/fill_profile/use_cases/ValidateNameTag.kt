package com.example.movio.feature.account_setup.fill_profile.use_cases

import com.example.movio.feature.common.status.ValidationResultState

class ValidateNameTag {
    fun execute(nameTag: String) : ValidationResultState{
        var isValid: ValidationResultState = ValidationResultState.Success

        // TODO add name tag validation business logic

        return isValid
    }
}