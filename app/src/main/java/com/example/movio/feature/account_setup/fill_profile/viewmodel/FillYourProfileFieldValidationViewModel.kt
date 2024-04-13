package com.example.movio.feature.account_setup.fill_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.movio.core.MovioApplication
import com.example.movio.feature.account_setup.fill_profile.models.Profile
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateNameTag
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidatePhoneNumber
import com.example.movio.feature.common.status.ValidationFailureTypes
import com.example.movio.feature.common.status.ValidationResultState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FillYourProfileFieldValidationViewModel(
    private val validateFullName: ValidateFullName,
    private val validateNameTag: ValidateNameTag,
    private val validatePhoneNumber: ValidatePhoneNumber
) : ViewModel(){

    companion object{
        val factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MovioApplication
                return FillYourProfileFieldValidationViewModel(
                    application.movioContainer.validateFullName,
                    application.movioContainer.validateNameTag,
                    application.movioContainer.validatePhoneNumber
                ) as T
            }
        }
    }


    private val _fullNameUiState: MutableSharedFlow<ValidationResultState> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val fullNameUiState = _fullNameUiState.asSharedFlow()


    private val _nameTagUiState: MutableSharedFlow<ValidationResultState> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val nameTagUiState = _nameTagUiState.asSharedFlow()

    private val _phoneNumberUiState: MutableSharedFlow<ValidationResultState> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val phoneNumberUiState = _phoneNumberUiState.asSharedFlow()

    private val _continueUiState: MutableStateFlow<ValidationResultState> = MutableStateFlow(ValidationResultState.Neutral)
    val continueUiState = _continueUiState.asStateFlow()

    init {
        _fullNameUiState.tryEmit(ValidationResultState.Neutral)
        _nameTagUiState.tryEmit(ValidationResultState.Neutral)
        _phoneNumberUiState.tryEmit(ValidationResultState.Neutral)
    }

    fun validate(profile: Profile){
        val fullNameState = validateFullName.execute(profile.fullName)
        val nameTagState = validateNameTag.execute(profile.nameTag)
        val phoneNumberState = validatePhoneNumber.execute(profile.nameTag)

        _fullNameUiState.tryEmit(fullNameState)
        _nameTagUiState.tryEmit(nameTagState)
        _phoneNumberUiState.tryEmit(phoneNumberState)

        if(
            nameTagState == ValidationResultState.Success
            &&
            phoneNumberState == ValidationResultState.Success
            ){
            _continueUiState.value = ValidationResultState.Success
        }else{
            _continueUiState.value = ValidationResultState.Failure(ValidationFailureTypes.UNABLE_TO_CONTINUE)
        }
    }
}