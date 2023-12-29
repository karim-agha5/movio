package com.example.movio.feature.authentication.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.navigation.CoordinatorHost
import com.example.movio.databinding.FragmentAuthenticationBinding
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationFragment :
    BaseFragment<FragmentAuthenticationBinding>(),AuthenticationResultCallbackLauncher,
    CoordinatorHost {

/*    private val userManager by lazy { movioApplication.movioContainer.userManager }
    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var twitterSignInService: TwitterAuthenticationService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var disposable: Disposable
    private val authenticationHelper by lazy  {movioApplication.movioContainer.authenticationHelper }

 */
    override val coordinator by lazy {
        movioApplication.movioContainer.rootCoordinator.requireCoordinator()
    }

    private val authenticationViewModel by lazy {
        coordinator
            .requireViewModel<LoginCredentials,SignInActions,SignInStatus>(this::class.java)
        as FederatedAuthenticationBaseViewModel
    }
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver

    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationViewModel.register(requireActivity())
        authenticationViewModel.register(this)
        // Register the authentication lifecycle observer
        // to unregister the launcher when the Lifecycle is destroyed.
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(requireActivity().activityResultRegistry,authenticationViewModel.getGoogleSignInService())
        lifecycle.addObserver(authenticationLifecycleObserver)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthenticationBinding {
        return FragmentAuthenticationBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        binding.btnSignInWithPassword.setOnClickListener {
            lifecycleScope.launch {
                coordinator.postAction(AuthenticationActions.ToSignInScreen)
            }
        }

        binding.btnSignup.setOnClickListener {
            lifecycleScope.launch {
                coordinator.postAction(AuthenticationActions.ToEmailAndPasswordScreen)
            }
        }

        binding.btnContinueWithFacebook.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
            /*TODO implement when the app is published*/
        }

        binding.btnContinueWithTwitter.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { startTwitterAuthenticationFlow() }
        }

        binding.btnContinueWithGoogle.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { startGoogleAuthenticationFlow() }
        }



        val source = authenticationHelper.getAuthenticationResultObservableSource()
        disposable = source.subscribe{
            /**
             * TODO should handle the cases were null is sent from the source observable.
             *  Look [TwitterAuthenticationService]
             */
            when(it){
                is AuthenticationResult.Success -> onSuccessfulAuthentication(it.user)
                is AuthenticationResult.Failure -> showAppropriateDialog(it.throwable)
            }
        }
        */

        binding.btnContinueWithFacebook.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.FacebookClicked) }
        binding.btnContinueWithGoogle.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.GoogleClicked) }
        binding.btnContinueWithTwitter.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.TwitterClicked) }
        binding.btnSignup.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.SignupClicked) }
        binding.btnSignInWithPassword.setOnClickListener { authenticationViewModel.postAction(null,SignInActions.SignInClicked) }

        authenticationViewModel.result.observe(viewLifecycleOwner){ onResultReceived(it) }

    }
/*
    private suspend fun startGoogleAuthenticationFlow(){
        googleSignInService.login(null)
    }

    private suspend fun startTwitterAuthenticationFlow(){
            twitterSignInService.login(null)
    }
*/
    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest){
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun onResultReceived(signInStatus: SignInStatus){
        when(signInStatus){
            is SignInStatus.SignInFailed    -> showAppropriateDialog(signInStatus.throwable)
            else                            -> { /*Do Nothing*/ }
        }
    }
/*
    private fun navigateToHomeFragment() {
        lifecycleScope.launch {
            coordinator.postAction(AuthenticationActions.ToHomeScreen)
        }
    }
*
    private fun onSuccessfulAuthentication(firebaseUser: FirebaseUser?){
        userManager.authenticateUser(firebaseUser)
        navigateToHomeFragment()
    }
*/
    private fun showAppropriateDialog(throwable: Throwable?){
        if(throwable is ApiException) showDialog(throwable.statusCode)
        else showDialog(throwable?.message)
    }

    private fun showDialog(message: String?){
        lifecycleScope.launch(Dispatchers.Main){
            buildDialog(
                getString(R.string.generic_authentication_error_title),
                message
            ).show()
        }
    }
    private fun showDialog(statusCode: Int){
        val title: String
        val message: String
        when(statusCode) {
            CommonStatusCodes.CANCELED  -> {
                 title = getString(R.string.canceled_authentication_title)
                 message = getString(R.string.canceled_authentication_message)
            }
            CommonStatusCodes.NETWORK_ERROR -> {
                 title = getString(R.string.canceled_authentication_title)
                 message = getString(R.string.canceled_authentication_message)
            }
            else -> {
                title = getString(R.string.generic_authentication_error_title)
                message = getString(R.string.generic_authentication_error_message)
            }
        }
        // Show the dialog on the main thread
        // because this function is called from an observer on a background thread
         lifecycleScope.launch(Dispatchers.Main){ buildDialog(title,message).show() }
    }

    private fun buildDialog(title: String,message: String?) : MaterialAlertDialogBuilder{
        val defaultMessage = getString(R.string.generic_authentication_error_message)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message ?: defaultMessage)
            .setNeutralButton(getString(R.string.ok)) { _, _ -> /* Do nothing*/ }
    }

    override fun onDestroy() {
        super.onDestroy()
        authenticationViewModel.unregister()
        //authenticationHelper.disposeAuthenticationResult(disposable)
    }
}