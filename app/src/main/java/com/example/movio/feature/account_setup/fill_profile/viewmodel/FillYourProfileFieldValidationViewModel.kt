package com.example.movio.feature.account_setup.fill_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.movio.core.MovioApplication
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName

class FillYourProfileFieldValidationViewModel(
    private val validateFullName: ValidateFullName
) : ViewModel(){



    companion object{
        val factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovioApplication
                return FillYourProfileFieldValidationViewModel(application.movioContainer.validateFullName) as T
            }
        }
    }
}