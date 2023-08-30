package com.example.movio.feature.authentication.helpers

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

object AuthenticationHelper {

    // User a Subject to manually call the onNext() when needed
    // without the need to pass an emitter at the time of the creation
    private val authenticationResultSource: PublishSubject<AuthenticationResult> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    fun getAuthenticationResultObservableSource() = authenticationResultSource

    fun onFailure(throwable: Throwable?){
       authenticationResultSource.onNext(AuthenticationResult.Failure(throwable))
    }

    fun onSuccess(firebaseUser: FirebaseUser?){
        authenticationResultSource.onNext(AuthenticationResult.Success(firebaseUser))
    }

    fun disposeAuthenticationResult(disposable: Disposable){
        compositeDisposable.add(disposable)
        compositeDisposable.dispose()
    }

    fun signOut(){
        Firebase.auth.signOut()
    }
}