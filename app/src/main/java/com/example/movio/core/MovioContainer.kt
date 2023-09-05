package com.example.movio.core

import com.example.movio.core.common.RootCoordinator
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A container of objects that are shared across the whole app.
 * */
class MovioContainer{

    val rootCoordinator = RootCoordinator()
    val firebaseAuth = Firebase.auth
    val authenticationHelper = AuthenticationHelper
    val userManager = UserManager.getInstance(firebaseAuth)

}