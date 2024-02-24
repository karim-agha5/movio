package com.example.movio.feature.authentication.navigation.viewmodelsfactory

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModel
import com.example.movio.feature.authentication.signin.viewmodel.SignInViewModelFactory
import com.example.movio.feature.authentication.signin.views.SignInFragment
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModelFactory
import com.example.movio.feature.authentication.signup.views.SignupFragment
import com.example.movio.feature.authentication.viewmodels.AuthenticationViewModel
import com.example.movio.feature.authentication.viewmodels.AuthenticationViewModelFactory
import com.example.movio.feature.authentication.views.AuthenticationFragment
import com.example.movio.feature.common.helpers.UserManager
import com.example.movio.feature.splash.viewmodel.SplashViewModel
import com.example.movio.feature.splash.viewmodel.SplashViewModelFactory
import com.example.movio.feature.splash.views.SplashFragment
import com.google.firebase.auth.FirebaseAuth

// TODO refactor this class to inherit AndroidViewModelFactory
class AuthenticationViewModelsFactory(
    private val application: Application,
    //private val firebaseAuth: FirebaseAuth,
    //private val authenticationHelper: AuthenticationHelper,
    //private val userManager: UserManager
) {

    private val movioContainer = (application as MovioApplication).movioContainer
    private fun createSplashViewModel() : SplashViewModel{
        val factory = SplashViewModelFactory(movioContainer.userManager,application)
        return factory.create(SplashViewModel::class.java)
    }
     private fun createSignupViewModel() : SignupViewModel {
        val service =
            EmailAndPasswordAuthenticationService
                .getInstance(movioContainer.firebaseAuth)

        val factory = SignupViewModelFactory(
            //service,
            //(application as MovioApplication).movioContainer.googleSignInService,
            //application.movioContainer.twitterAuthenticationService,
            //authenticationHelper,
            application,
            movioContainer.authenticationRepository
        )
        return factory.create(SignupViewModel::class.java)
    }

    private fun createSignInViewModel() : SignInViewModel {
        val service =
            EmailAndPasswordAuthenticationService
                .getInstance(movioContainer.firebaseAuth)

        val factory = SignInViewModelFactory(
            service,
            //(application as MovioApplication).movioContainer.googleSignInService,
            //application.movioContainer.twitterAuthenticationService,
            //authenticationHelper,
            application,
            movioContainer.authenticationRepository
        )
        return factory.create(SignInViewModel::class.java)
    }

    private fun createAuthenticationViewModel() : AuthenticationViewModel {
        val factory = AuthenticationViewModelFactory(application,movioContainer.authenticationRepository)
        return factory.create(AuthenticationViewModel::class.java)
    }

     fun createViewModel(cls: Class<out Fragment>) : BaseViewModel<out Data,out Action, out Status> {
        return when(cls){
            SplashFragment::class.java          -> createSplashViewModel()
            SignupFragment::class.java          -> createSignupViewModel()
            SignInFragment::class.java          -> createSignInViewModel()
            AuthenticationFragment::class.java  -> createAuthenticationViewModel()
            else                                -> throw IllegalArgumentException()
        }
    }

}