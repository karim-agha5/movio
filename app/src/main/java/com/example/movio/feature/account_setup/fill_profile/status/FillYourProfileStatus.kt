package com.example.movio.feature.account_setup.fill_profile.status

import com.example.movio.core.common.Status

sealed class FillYourProfileStatus : Status {
    object UnavailableNameTag: FillYourProfileStatus()

    object IncorrectPhoneNumber: FillYourProfileStatus()

    
}