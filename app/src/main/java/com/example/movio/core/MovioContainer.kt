package com.example.movio.core

import com.example.movio.core.helpers.ViewModelsFactoryProvider
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A container of objects that are shared across the whole app.
 * */
class MovioContainer{

    private val viewModelsFactoryProvider = ViewModelsFactoryProvider
    val firebaseAuth = Firebase.auth
    val authenticationHelper = AuthenticationHelper
    val rootCoordinator = RootCoordinator(viewModelsFactoryProvider,firebaseAuth, authenticationHelper)
    val userManager = UserManager.getInstance(firebaseAuth)

}