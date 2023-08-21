package com.example.movio.feature.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.movio.NavGraphDirections
import com.example.movio.R
import com.example.movio.feature.common.helpers.UserManager
import com.example.movio.feature.home.HomeFragmentDirections
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private val userManager = UserManager.getInstance(Firebase.auth)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        Handler(Looper.getMainLooper()).postDelayed({navigateFromSplash()},2000)
    }

    /*
    * Navigates to either the AuthenticationFragment or to the HomeFragment
    * */
    private fun navigateFromSplash(){
        if(userManager.isLoggedIn()){
            findNavController()
                .navigate(
                    HomeFragmentDirections.actionGlobalHomeFragment(),
                    navOptions {
                        anim {
                            enter = android.R.anim.fade_in
                            exit = android.R.anim.fade_out
                        }
                        popUpTo(R.id.splashFragment){
                            inclusive = true
                        }
                    }
                )
        }
        else{
            findNavController()
                .navigate(
                    NavGraphDirections.actionGlobalAuthenticationFragment(),
                    navOptions {
                        anim {
                            enter = android.R.animator.fade_in
                            exit = android.R.animator.fade_out
                        }
                        popUpTo(R.id.splashFragment){
                            inclusive = true
                        }
                    }
                )
        }
    }
}