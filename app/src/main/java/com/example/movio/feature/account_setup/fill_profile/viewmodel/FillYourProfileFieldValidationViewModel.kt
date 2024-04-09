package com.example.movio.feature.account_setup.fill_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.movio.core.MovioApplication
import com.example.movio.feature.account_setup.fill_profile.models.Profile
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName
import com.example.movio.feature.common.status.ValidationResultState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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


    private val _fullNameUiState: MutableSharedFlow<ValidationResultState> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val fullNameUiState = _fullNameUiState.asSharedFlow()


    init {
        _fullNameUiState.tryEmit(ValidationResultState.Neutral)
    }

    fun validate(profile: Profile){
        _fullNameUiState.tryEmit(validateFullName.execute(profile.fullName))
    }
}