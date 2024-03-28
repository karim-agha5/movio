package com.example.movio.feature.splash.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleEventObserver
import com.example.movio.core.common.BaseFragment
import com.example.movio.databinding.FragmentSplashBinding
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.status.UserAuthenticationStatus

class SplashFragment : BaseFragment<FragmentSplashBinding,Nothing,AuthenticationActions, UserAuthenticationStatus>(SplashFragment::class.java),LifecycleEventObserver {

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Authentication or Home after 2 secs with a fading animation.
        Handler(Looper.getMainLooper()).postDelayed({ navigateFromSplash() }, 2000)
    }

    /*
    * Navigates to either the AuthenticationFragment or to the HomeFragment
    * */
    private fun navigateFromSplash(){
        viewModel.postAction(null,AuthenticationActions.FromSplashScreen)
    }
}