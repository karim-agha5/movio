package com.example.movio.feature.authentication.navigation.viewmodelsfactory

import android.app.Application
import androidx.fragment.app.Fragment
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.feature.account_setup.fill_profile.viewmodel.FillYourProfileViewModel
import com.example.movio.feature.account_setup.fill_profile.viewmodel.FillYourProfileViewModelFactory
import com.example.movio.feature.account_setup.fill_profile.views.FillYourProfileFragment
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModel
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModelFactory
import com.example.movio.feature.authentication.signin.views.SignInFragment
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModelFactory
import com.example.movio.feature.authentication.signup.views.SignupFragment
import com.example.movio.feature.authentication.viewmodels.AuthenticationViewModel
import com.example.movio.feature.authentication.viewmodels.AuthenticationViewModelFactory
import com.example.movio.feature.authentication.views.AuthenticationFragment
import com.example.movio.feature.splash.viewmodel.SplashViewModel
import com.example.movio.feature.splash.viewmodel.SplashViewModelFactory
import com.example.movio.feature.splash.views.SplashFragment

// TODO refactor this class to inherit AndroidViewModelFactory
class AuthenticationViewModelsFactory(
    private val application: Application,
) {

    private val movioContainer = (application as MovioApplication).movioContainer
    private fun createSplashViewModel() = SplashViewModelFactory(movioContainer.userManager,application).create(SplashViewModel::class.java)

     private fun createSignupViewModel() =
         SignupViewModelFactory(application, movioContainer.authenticationRepository).create(SignupViewModel::class.java)


    private fun createSignInViewModel() =
        SignInViewModelFactory(
            application,
            movioContainer.authenticationRepository
        ).create(SignInViewModel::class.java)


    private fun createAuthenticationViewModel() =
        AuthenticationViewModelFactory(application,movioContainer.authenticationRepository).create(AuthenticationViewModel::class.java)


    private fun createFillYourProfileViewModel() = FillYourProfileViewModelFactory(application).create(FillYourProfileViewModel::class.java)

     fun createViewModel(cls: Class<out Fragment>) : BaseViewModel<out Data,out Action, out Status> {
        return when(cls){
            SplashFragment::class.java          -> createSplashViewModel()
            SignupFragment::class.java          -> createSignupViewModel()
            SignInFragment::class.java          -> createSignInViewModel()
            AuthenticationFragment::class.java  -> createAuthenticationViewModel()
            FillYourProfileFragment::class.java -> createFillYourProfileViewModel()
            else                                -> throw IllegalArgumentException()
        }
    }

}