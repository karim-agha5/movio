package com.example.movio.feature.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.PopUpToBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.movio.R
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {

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

        // Navigate to HomeScreen after 3 secs with fading animation.
        Handler().postDelayed({
            findNavController()
                .navigate(
                    SplashFragmentDirections.actionSplashFragmentToAuthenticationFragment(),
                    navOptions {
                        anim {
                            enter = android.R.animator.fade_in
                            exit = android.R.animator.fade_out
                        }
                        popUpTo(R.id.splashFragment){
                            this.inclusive = true
                        }
                    }
                )
        },2000)
    }
}