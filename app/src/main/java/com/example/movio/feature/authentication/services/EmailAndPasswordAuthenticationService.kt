package com.example.movio.feature.authentication.services

import android.util.Log
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.common.models.SignupCredentials
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.Exception
import kotlin.Throws

// TODO refactor the suspend functions as they're unreadable
class EmailAndPasswordAuthenticationService private constructor(
    private val firebaseAuth: FirebaseAuth,
) {


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
    @Throws(FirebaseAuthUserCollisionException::class)
    private suspend fun launchSignupWithEmailAndPassword(
        credentials: SignupCredentials
    ) : FirebaseUser?{

        /*withContext(Dispatchers.IO){
            val deferredResult = async {
                try {
                    firebaseAuth
                        .createUserWithEmailAndPassword(credentials.email,credentials.password)
                        .await()
                }catch (e: Exception){ e}
            }

            val result = deferredResult.await()
            if(result is FirebaseAuthUserCollisionException){
                throw result
            }else{
                firebaseUser = (result as AuthResult).user
                firebaseAuth.signOut()
                firebaseUser?.sendEmailVerification()
            }
        }*/


        var firebaseUser: FirebaseUser? = firebaseAuth
            .createUserWithEmailAndPassword(credentials.email, credentials.password)
            .await()
            .user

        firebaseUser?.sendEmailVerification()
        firebaseAuth.signOut()

        return firebaseUser
    }
    @Throws(Exception::class)
     suspend fun login(credentials: LoginCredentials?) : FirebaseUser?{
         var firebaseUser: FirebaseUser? = null

         if(credentials != null){
             /*withContext(Dispatchers.IO){
                 val deferredResult = async {
                     try {
                         firebaseAuth
                             .signInWithEmailAndPassword(credentials.email,credentials.password)
                             .await()
                     }catch(e: Exception){e}
                 }

                 val result = deferredResult.await()
                 if(result is FirebaseAuthInvalidUserException){
                     throw FirebaseAuthInvalidUserException("message","Invalid username or password")
                 }else{
                     firebaseUser = (result as AuthResult).user
                 }
             }*/

             try{
                 firebaseUser = firebaseAuth
                     .signInWithEmailAndPassword(credentials.email,credentials.password)
                     .await()
                     .user
             }catch (ex: Exception){
                 if (ex is FirebaseAuthInvalidUserException){
                     throw FirebaseAuthInvalidUserException("message","Invalid username or password")
                 }
             }
         }
         else{
            throw FirebaseAuthInvalidUserException("Invalid credentials","you must fill those empty fields")
         }

         return firebaseUser
    }
}