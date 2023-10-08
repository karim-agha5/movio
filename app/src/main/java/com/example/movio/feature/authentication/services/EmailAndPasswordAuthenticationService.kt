package com.example.movio.feature.authentication.services

import com.example.movio.feature.authentication.helpers.BaseCredentials
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception

class EmailAndPasswordAuthenticationService private constructor(
    private val firebaseAuth: FirebaseAuth,
) /*: SignupServiceContract<SignupCredentials>,LoginServiceContract<LoginCredentials>*/ {


    companion object{
        @Volatile
        private var instance: EmailAndPasswordAuthenticationService? = null

        fun getInstance(firebaseAuth: FirebaseAuth): EmailAndPasswordAuthenticationService{
            return instance ?: synchronized(this){
                EmailAndPasswordAuthenticationService(firebaseAuth).also { instance = it }
            }
        }
    }

    suspend fun signup(credentials: SignupCredentials?) : FirebaseUser? {
        var firebaseUser: FirebaseUser? = null

        if(credentials != null){
            firebaseUser = launchSignupWithEmailAndPassword(credentials)
        }
        else{
            // TODO implement later
        }

        return firebaseUser
    }

    /**
     * Launches a coroutine that attempts to create a new user with the specified credentials,
     * Sends the email in the credentials a verification link, and then signs out the user because
     * once a user is returned, they're automatically signed in even if their email isn't verified.
     * */
    private suspend fun launchSignupWithEmailAndPassword(
        credentials: SignupCredentials
    ) : FirebaseUser?{

        var firebaseUser: FirebaseUser? = null

        withContext(Dispatchers.IO){
            val deferredResult = async {
                try {
                    firebaseAuth
                        .createUserWithEmailAndPassword(credentials.email,credentials.password)
                        .await()
                }catch (e: Exception){ e}
            }
            firebaseUser = (deferredResult.await() as AuthResult).user
            firebaseAuth.signOut()
            firebaseUser?.sendEmailVerification()
        }

        return firebaseUser
    }
     suspend fun login(credentials: LoginCredentials?) : FirebaseUser?{
         var firebaseUser: FirebaseUser? = null

         if(credentials != null){
             withContext(Dispatchers.IO){
                 val deferredResult = async {
                     try {
                         firebaseAuth
                             .signInWithEmailAndPassword(credentials.email,credentials.password)
                             .await()
                     }catch(e: Exception){e}
                 }

                 firebaseUser = (deferredResult.await() as AuthResult).user
                 if(firebaseUser?.isEmailVerified == false){
                     firebaseAuth.signOut()
                 }
             }
         }
         else{
            // TODO implement later
         }

         return firebaseUser
    }
}