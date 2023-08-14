package com.example.movio.feature.authentication.helpers

import com.google.firebase.auth.FirebaseUser

/*
* For the other layers to know the result of the authentication operation.
* */
sealed class AuthenticationResult{
    class Success(
        val user: FirebaseUser?
    ) : AuthenticationResult()
    class Failure(
        val throwable: Throwable?
    ) : AuthenticationResult()
    // TODO add Cancellation as a result later on
}
