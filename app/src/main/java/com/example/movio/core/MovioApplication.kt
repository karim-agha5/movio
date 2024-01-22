package com.example.movio.core

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class MovioApplication : Application() {

    companion object{
        lateinit var appContext: Context
    }
    lateinit var movioContainer: MovioContainer
    override fun onCreate() {
        super.onCreate()
        movioContainer = MovioContainer(this)
        FirebaseApp.initializeApp(this)
        appContext = applicationContext
    }
}