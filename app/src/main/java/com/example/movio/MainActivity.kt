package com.example.movio

import android.os.Bundle
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.movio.core.MovioApplication
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher

class MainActivity : AppCompatActivity()/*, AuthenticationResultCallbackLauncher*/ {

    private val userManager by lazy { (application as MovioApplication).movioContainer.userManager }
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //(application as MovioApplication).movioContainer.initDependenciesOnActivityInstance(this,this)
        initRootCoordinator()
        //initAuthenticationObserver()
/*
        authenticationLifecycleObserver = AuthenticationLifecycleObserver(
            this.activityResultRegistry,
            (application as MovioApplication).movioContainer.googleSignInService
        )
        lifecycle.addObserver(authenticationLifecycleObserver)

 */
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
/*
    private fun initAuthenticationObserver(){
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(
                activityResultRegistry,
                (application as MovioApplication).movioContainer.googleSignInService
            )
        lifecycle.addObserver(authenticationLifecycleObserver)
    }
*/
    override fun onStart() {
        super.onStart()
        userManager.authenticateUser((application as MovioApplication).movioContainer.firebaseAuth.currentUser)
        /*if(userManager.isLoggedIn()){
            Log.i(tag, "found a user in activity | ")
        }
        else{
            Log.i(tag, "couldn't find a user in activity | ")
        }*/
    }
/*
    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest) {
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

 */
}