package com.example.movio.feature.splash.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.SplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.MovioApplication
import com.example.movio.feature.common.actions.AuthenticationActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val userManager by lazy {
        (requireActivity().application as MovioApplication).movioContainer.userManager
    }

    private val coordinator by lazy {
        (requireActivity().application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Authentication or Home after 2 secs with a fading animation.
        Handler(Looper.getMainLooper())
            .postDelayed(
            {
                lifecycleScope.launch(Dispatchers.Main) {
                    navigateFromSplash()
                }
            },
            2000
        )
    }

    /*
    * Navigates to either the AuthenticationFragment or to the HomeFragment
    * */
    private suspend fun navigateFromSplash(){
        if(userManager.isLoggedIn()){
            coordinator.postAction(AuthenticationActions.ToHomeScreen)
        }
        else{
            coordinator.postAction(AuthenticationActions.ToAuthenticationScreen)
        }
    }
}