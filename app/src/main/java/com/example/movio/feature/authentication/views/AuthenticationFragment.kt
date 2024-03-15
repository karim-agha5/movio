package com.example.movio.feature.authentication.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.movio.MainActivity
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.Experimental
import com.example.movio.core.helpers.Event
import com.example.movio.core.navigation.CoordinatorHost
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.databinding.FragmentAuthenticationBinding
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.signin.views.SignInFragment
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.MessageShower
import com.example.movio.feature.common.models.LoginCredentials
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

class AuthenticationFragment :
    BaseFragment
    <
            FragmentAuthenticationBinding,
            LoginCredentials,
            SignInActions,
            Event<SignInStatus>
    > (AuthenticationFragment::class.java),
    AuthenticationResultCallbackLauncher,
    CoordinatorHost{

    override val coordinator by lazy {
        movioApplication.movioContainer.rootCoordinator.requireCoordinator()
    }

    //private lateinit var authenticationViewModel: FederatedAuthenticationBaseViewModel<LoginCredentials,SignInActions, Event<SignInStatus>>
    private lateinit var authenticationViewModel: FederatedAuthenticationBaseViewModel<LoginCredentials,SignInActions, Event<SignInStatus>>
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
        authenticationViewModel.register(this)
    }

    /**
     * Necessary for the [SignInFragment] instantiation.
     * The instantiation of the [SignInFragment] requires the [MainActivity] onCreate() lifecycle
     * callback to be called first so that it initializes the [RootCoordinator] state correctly.
     * Therefore, an observation on the [MainActivity] lifecycle is necessary.
     * The observation on the [MainActivity] lifecycle is registered in the [onAttach] lifecycle callback.
     * */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        super.onStateChanged(source, event)
        if (event == Lifecycle.Event.ON_CREATE) {
            /*val vm by ViewModelDelegate<LoginCredentials,SignInActions, Event<SignInStatus>>(movioApplication,this::class.java)
            authenticationViewModel = vm as FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>*/
            authenticationViewModel = viewModel as FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>
            authenticationViewModel.register(requireActivity())
            authenticationLifecycleObserver =
                AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,authenticationViewModel::authenticateWithFirebase)
            lifecycle.addObserver(authenticationLifecycleObserver)
            lifecycle.addObserver(authenticationViewModel)
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthenticationBinding {
        return FragmentAuthenticationBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinueWithFacebook.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.FacebookClicked) }
        binding.btnContinueWithGoogle.setOnClickListener {
            startGoogleAuthenticationLoading()
            authenticationViewModel.postAction(null,SignInActions.GoogleClicked)
        }
        binding.btnContinueWithTwitter.setOnClickListener {
            startTwitterAuthenticationLoading()
            authenticationViewModel.postAction(null,SignInActions.TwitterClicked)
        }
        binding.btnSignup.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.SignupClicked) }
        binding.btnSignInWithPassword.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.SignInClicked) }

        authenticationViewModel.result.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { status -> onResultReceived(status) }
        }


    }
    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest){
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }


    private fun onResultReceived(signInStatus: SignInStatus) =
        when(signInStatus){
            is SignInStatus.SignInFailed    -> {
                @OptIn(Experimental::class)
                MessageShower.showAppropriateErrorDialog(requireContext(),signInStatus.throwable)
                stopGoogleAuthenticationLoading()
                stopTwitterAuthenticationLoading()
            }
            else                            -> { /*Do Nothing*/ }
        }

    private fun prepareAuthenticationLoading(){
        val spec = CircularProgressIndicatorSpec(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
         progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(requireContext(), spec)
    }

    private fun startGoogleAuthenticationLoading(){
        binding.btnContinueWithGoogle.icon = progressIndicatorDrawable
        binding.btnContinueWithGoogle.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnContinueWithGoogle.text = getString(R.string.attempting_to_sign_in_message)
    }

    private fun startTwitterAuthenticationLoading(){
        binding.btnContinueWithTwitter.icon = progressIndicatorDrawable
        binding.btnContinueWithTwitter.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnContinueWithTwitter.text = getString(R.string.attempting_to_sign_in_message)
    }

    private fun stopGoogleAuthenticationLoading(){
        binding.btnContinueWithGoogle.icon = ResourcesCompat.getDrawable(resources,R.drawable.google_circular_icon,context?.theme)
        binding.btnContinueWithGoogle.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnContinueWithGoogle.text = getString(R.string.btn_google_authentication_text)
    }

    private fun stopTwitterAuthenticationLoading(){
        binding.btnContinueWithTwitter.icon = ResourcesCompat.getDrawable(resources,R.drawable.twitter_icon,context?.theme)
        binding.btnContinueWithTwitter.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnContinueWithTwitter.text = getString(R.string.btn_twitter_authentication_text)
    }
}