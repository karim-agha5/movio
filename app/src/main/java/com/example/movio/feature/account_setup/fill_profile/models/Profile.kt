package com.example.movio.feature.account_setup.fill_profile.models

import com.example.movio.feature.account_setup.fill_profile.status.Sex

data class Profile(
    val fullName: String,
    val nameTag: String,
    val sex: Sex = Sex.MALE,
    val phoneNumber: String
)