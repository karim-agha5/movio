package com.example.movio.feature.authentication.services

import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.helpers.SignupCredentials
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EmailAndPasswordAuthenticationService private constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authenticationHelper: AuthenticationHelper
) :
    SignupServiceContract<SignupCredentials>,LoginServiceContract<LoginCredentials> {


    companion object{
        @Volatile
        private var instance: EmailAndPasswordAuthenticationService? = null

        fun getInstance(
            firebaseAuth: FirebaseAuth,
            authenticationHelper: AuthenticationHelper
        ): EmailAndPasswordAuthenticationService{
            return instance ?: synchronized(this){
                EmailAndPasswordAuthenticationService(firebaseAuth,authenticationHelper).also { instance = it }
            }
        }
    }

    override suspend fun signup(credentials: SignupCredentials?) {
        if(credentials != null){
            withContext(Dispatchers.IO){
                val deferredResult = async {
                    try{
                        firebaseAuth
                            .createUserWithEmailAndPassword(credentials.email,credentials.password)
                            .await()
                    }catch (e: Exception){ e }
                }

                try{
                    val user = (deferredResult.await() as AuthResult).user
                    authenticationHelper.onSuccess(user)

                }catch (e: Exception){
                    authenticationHelper.onFailure(e)
                }
            }

        }
        else{
            // TODO implement later
        }
    }

    override suspend fun login(credentials: LoginCredentials?) {
        if(credentials != null){
            withContext(Dispatchers.IO){
                val deferredResult = async {
                    try{
                        firebaseAuth
                            .signInWithEmailAndPassword(credentials.email,credentials.password)
                            .await()
                    }catch (e: Exception){ e }
                }

                try{
                    val user = (deferredResult.await() as AuthResult).user
                    authenticationHelper.onSuccess(user)

                }catch (e: Exception){
                    authenticationHelper.onFailure(e)
                }
            }

        }
        else{
            // TODO implement later
        }
    }
}