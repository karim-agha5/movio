package com.example.movio.feature.account_setup.fill_profile.use_cases

class ValidateFullName {
    fun execute(fullName: String?) : Boolean{
        var isValid = true

        if(fullName?.isBlank() == true || fullName?.isEmpty() == true){
            isValid = false
        }

        return isValid
    }
}