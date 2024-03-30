package com.example.movio.feature.account_setup.fill_profile.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.helpers.CoordinatorDelegate
import com.example.movio.core.navigation.Coordinator
import com.example.movio.feature.account_setup.fill_profile.actions.FillYourProfileActions
import com.example.movio.feature.account_setup.fill_profile.models.Profile
import com.example.movio.feature.account_setup.fill_profile.status.FillYourProfileStatus

class FillYourProfileViewModel(
    private val application: Application
) : BaseViewModel<Profile,FillYourProfileActions,FillYourProfileStatus>(application){

    override val coordinator: Coordinator by CoordinatorDelegate(application as MovioApplication)

    override val result: LiveData<FillYourProfileStatus>
        get() = TODO("Not yet implemented")

    override fun postActionOnSuccess() {
        TODO("Not yet implemented")
    }

    override fun postActionOnFailure(throwable: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun onPostResultActionExecuted(action: FillYourProfileActions) {
        TODO("Not yet implemented")
    }

    override fun postAction(data: Profile?, action: FillYourProfileActions) {
        TODO("Not yet implemented")
    }
}