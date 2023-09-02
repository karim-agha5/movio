package com.example.movio.feature.authentication.signup.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.databinding.FragmentSignupBinding
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.navigation.AuthenticationActions
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.common.helpers.UserManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseUser
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupFragment : BaseFragment<FragmentSignupBinding>(),AuthenticationResultCallbackLauncher {

    private val coordinator by lazy { movioApplication.movioContainer.rootCoordinator.requireCoordinator() }
    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInService = GoogleSignInService.getInstance(requireActivity(),this)
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(requireActivity().activityResultRegistry,googleSignInService)
        lifecycle.addObserver(authenticationLifecycleObserver)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignupBinding {
        return FragmentSignupBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/}
        binding.btnGoogle.setOnClickListener { lifecycleScope.launch { startGoogleAuthenticationFlow() } }
        binding.btnTwitter.setOnClickListener { lifecycleScope.launch { startTwitterAuthenticationFlow() } }

        val source = AuthenticationHelper.getAuthenticationResultObservableSource()
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
    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest) {
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private suspend fun startGoogleAuthenticationFlow(){
        googleSignInService.login(null)
    }

    private suspend fun startTwitterAuthenticationFlow(){
        TwitterAuthenticationService
            .getInstance(
                requireActivity(),
                movioApplication.movioContainer.firebaseAuth,
                AuthenticationHelper
            )
            .login(null)
    }

    private fun onSuccessfulAuthentication(firebaseUser: FirebaseUser?){
       authenticateUser(firebaseUser)
        navigateToHome()
    }

    private fun authenticateUser(firebaseUser: FirebaseUser?){
        val userManager = UserManager.getInstance(movioApplication.movioContainer.firebaseAuth)
        userManager.authenticateUser(firebaseUser)
    }

    private fun navigateToHome(){
        lifecycleScope.launch { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }

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

    private fun buildDialog(title: String,message: String?) : MaterialAlertDialogBuilder {
        val defaultMessage = getString(R.string.generic_authentication_error_message)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message ?: defaultMessage)
            .setNeutralButton(getString(R.string.ok)) { _, _ -> /* Do nothing*/ }
    }

    override fun onDestroy() {
        super.onDestroy()
        AuthenticationHelper.disposeAuthenticationResult(disposable)
    }
}