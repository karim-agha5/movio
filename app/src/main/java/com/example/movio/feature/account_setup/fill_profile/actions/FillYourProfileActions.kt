package com.example.movio.feature.account_setup.fill_profile.actions

import com.example.movio.core.common.Action

sealed class FillYourProfileActions : Action{
    object ContinueClicked: FillYourProfileActions()

    object SkipClicked: FillYourProfileActions()
}