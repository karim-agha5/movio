package com.example.movio

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private val userManager = UserManager.getInstance(auth) // TODO add this to the app class
    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    override fun onStart() {
        super.onStart()
        userManager.authenticateUser(auth.currentUser)
        if(userManager.isLoggedIn()){
            Log.i(tag, "found a user in activity | ")
        }
        else{
            Log.i(tag, "couldn't find a user in activity | ")
        }
    }
}