package com.example.movio.feature.splash.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleEventObserver
import com.example.movio.MainActivity
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.databinding.FragmentSplashBinding
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.status.UserAuthenticationStatus
import com.example.movio.feature.splash.viewmodel.SplashViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding,Nothing,AuthenticationActions, UserAuthenticationStatus>(SplashFragment::class.java),LifecycleEventObserver {

    private lateinit var splashViewModel: BaseViewModel<Nothing,AuthenticationActions, UserAuthenticationStatus>

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }*/

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
        //splashViewModel.postAction(null,AuthenticationActions.FromSplashScreen)
        viewModel.postAction(null,AuthenticationActions.FromSplashScreen)
    }

    /**
     * Necessary for the [SplashViewModel] instantiation.
     * The instantiation of the [SplashViewModel] requires the [MainActivity] onCreate() lifecycle
     * callback to be called first so that it initializes the [RootCoordinator] state correctly.
     * Therefore, an observation on the [MainActivity] lifecycle is necessary.
     * The observation on the [MainActivity] lifecycle is registered in the [onAttach] lifecycle callback.
    * */
    /*override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            val vm by ViewModelDelegate<Nothing, AuthenticationActions, UserAuthenticationStatus>(
                movioApplication,
                this::class.java
            )
            splashViewModel = vm
        }
    }*/
}