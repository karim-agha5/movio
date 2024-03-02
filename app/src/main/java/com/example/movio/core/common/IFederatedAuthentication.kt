package com.example.movio.core.common

import com.google.firebase.auth.FirebaseUser

interface IFederatedAuthentication {
    fun authenticate() : FirebaseUser?
}