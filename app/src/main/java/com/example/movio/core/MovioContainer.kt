package com.example.movio.core

import android.app.Application
import com.example.movio.core.helpers.ViewModelsFactoryProvider
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.feature.account_setup.fill_profile.use_cases.ValidateFullName
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.services.EmailAndPasswordAuthenticationService
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.common.data_access.AuthenticationRepository
import com.example.movio.feature.common.helpers.UserManager
import com.example.movio.feature.common.use_cases.ValidateEmail
import com.example.movio.feature.common.use_cases.ValidatePassword
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A container of objects that are shared across the whole app.
 * */
class MovioContainer(private val application: Application){

    private val viewModelsFactoryProvider = ViewModelsFactoryProvider
    val firebaseAuth = Firebase.auth
    val authenticationHelper = AuthenticationHelper
    val userManager = UserManager.getInstance(firebaseAuth)
    val emailAndPasswordAuthenticationService = EmailAndPasswordAuthenticationService.getInstance(firebaseAuth)
    val googleSignInService: GoogleSignInService = GoogleSignInService.getInstance()
    val twitterAuthenticationService: TwitterAuthenticationService = TwitterAuthenticationService.getInstance(firebaseAuth,authenticationHelper)
    val rootCoordinator =
        RootCoordinator(application,viewModelsFactoryProvider,firebaseAuth, authenticationHelper,userManager)
    val validateEmail = ValidateEmail()
    val validatePassword = ValidatePassword()
    val validateFullName = ValidateFullName()
    val authenticationRepository = AuthenticationRepository(
        googleSignInService,
        twitterAuthenticationService,
        emailAndPasswordAuthenticationService
    )

    // Should be called by the start destination
 /*   fun initDependenciesOnActivityInstance(
        activity: ComponentActivity,
        launcher: AuthenticationResultCallbackLauncher
    ){
        googleSignInService = GoogleSignInService.getInstance(activity,launcher)
        twitterAuthenticationService = TwitterAuthenticationService.getInstance(activity,firebaseAuth, authenticationHelper)
    }*/
}