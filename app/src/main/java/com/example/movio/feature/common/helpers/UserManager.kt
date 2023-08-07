package com.example.movio.feature.common.helpers

import com.example.movio.feature.common.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserManager(
    private val firebaseAuth: FirebaseAuth
) {


    companion object{
        @Volatile
        private var instance: UserManager? = null

        fun getInstance(firebaseAuth: FirebaseAuth) : UserManager {
            return instance ?: synchronized(this){
                instance ?: UserManager(firebaseAuth).also { instance = it }
            }
        }
    }

    private var user: User? = null

    fun authenticateUser(firebaseUser: FirebaseUser?){
        if(firebaseUser != null) {
            user = User(
                firebaseUser.displayName ?: "Unknown",
                firebaseUser.email ?: "Unknown",
                null //firebaseUser.photoUrl TODO set the profile picture
            )
        }
    }

    fun unAuthenticateUser() {
        firebaseAuth.signOut()
        user = null
    }

    fun getUser() = user

    fun isLoggedIn() = user != null
}