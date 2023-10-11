package com.example.movio.feature.authentication.navigation.viewmodelsfactory

import androidx.fragment.app.Fragment
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.navigation.AuthenticationActions
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModel
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModelFactory
import com.example.movio.feature.authentication.signup.EmailVerificationStatus
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModelFactory
import com.example.movio.feature.authentication.signup.views.SignupFragment
import com.google.firebase.auth.FirebaseAuth

// TODO refactor this class to inherit AndroidViewModelFactory
class AuthenticationViewModelsFactory(
     val firebaseAuth: FirebaseAuth,
     val authenticationHelper: AuthenticationHelper
) {
     private fun createSignupViewModel() : SignupViewModel {
        val service =
            EmailAndPasswordAuthenticationService
                .getInstance(firebaseAuth)

        val factory = SignupViewModelFactory(service,authenticationHelper)
         return factory.create(SignupViewModel::class.java)
    }

    private fun createSignInViewModel() : BaseViewModel<LoginCredentials, AuthenticationActions, EmailVerificationStatus>{
        val service =
            EmailAndPasswordAuthenticationService
                .getInstance(firebaseAuth)

        val factory = SignInViewModelFactory(service,authenticationHelper)
        return factory.create(SignInViewModel::class.java)
    }

     fun createViewModel(cls: Class<out Fragment>) : BaseViewModel<out Data,out Action, out Status> {
        return when(cls){
            SignupFragment::class.java -> createSignupViewModel()
            else -> createSignInViewModel()
        }
    }

}