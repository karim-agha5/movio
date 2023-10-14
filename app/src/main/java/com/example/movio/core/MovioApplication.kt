package com.example.movio.core

import android.app.Application
import com.google.firebase.FirebaseApp

class MovioApplication : Application() {

    lateinit var movioContainer: MovioContainer
    override fun onCreate() {
        super.onCreate()
        movioContainer = MovioContainer(this)
        FirebaseApp.initializeApp(this)
    }
}