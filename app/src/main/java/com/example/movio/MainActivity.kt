package com.example.movio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.movio.core.MovioApplication

class MainActivity : AppCompatActivity() {

    private val userManager by lazy { (application as MovioApplication).movioContainer.userManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRootCoordinator()
    }


    private fun initRootCoordinator(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        (application as MovioApplication)
            .movioContainer
            .rootCoordinator
            .init(navController)
    }
    override fun onStart() {
        super.onStart()
        userManager.authenticateUser((application as MovioApplication).movioContainer.firebaseAuth.currentUser)
    }
}