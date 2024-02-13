package com.example.movio.feature.splash.views

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.helpers.ViewModelDelegate
import com.example.movio.core.navigation.CoordinatorHost
import com.example.movio.databinding.FragmentSplashBinding
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.status.UserAuthenticationStatus

class SplashFragment : BaseFragment<FragmentSplashBinding>(),LifecycleEventObserver {

   /*private val splashViewModel by lazy {
       movioApplication
           .movioContainer
           .rootCoordinator
           .requireViewModel<Nothing,AuthenticationActions,Nothing>(this::class.java)
   }*/



    private lateinit var splashViewModel: BaseViewModel<Nothing,AuthenticationActions, UserAuthenticationStatus>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "onCreate in fragment : ")

    }

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
        splashViewModel.postAction(null,AuthenticationActions.ToSplashScreen)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            val vm by ViewModelDelegate<Nothing, AuthenticationActions, UserAuthenticationStatus>(
                movioApplication,
                this::class.java
            )
            splashViewModel = vm
        }
    }
}