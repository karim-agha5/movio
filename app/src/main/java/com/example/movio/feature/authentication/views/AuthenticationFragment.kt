package com.example.movio.feature.authentication.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.CoordinatorHost
import com.example.movio.databinding.FragmentAuthenticationBinding
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationFragment :
    Fragment(),AuthenticationResultCallbackLauncher,CoordinatorHost {

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding
        get() = _binding!!
    private val firebaseAuth by lazy {Firebase.auth}
    private val userManager = UserManager.getInstance(firebaseAuth)
    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var disposable: Disposable
    private val authenticationHelper by lazy {AuthenticationHelper}
    override val coordinator by lazy {
        (requireActivity().application as MovioApplication).movioContainer.rootCoordinator.requireCoordinator()
    }

    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInService = GoogleSignInService.getInstance(requireActivity(),this)
        // Register the authentication lifecycle observer
        // to unregister the launcher when the Lifecycle is destroyed
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(requireActivity().activityResultRegistry,googleSignInService)
        lifecycle.addObserver(authenticationLifecycleObserver)
        val c = (requireActivity().application as MovioApplication).movioContainer.rootCoordinator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_authentication,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnContinueWithFacebook.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        }

        binding.btnContinueWithTwitter.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { twitterAuthenticationFlow() }
        }

        binding.btnContinueWithGoogle.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { googleAuthenticationFlow() }
        }
        val source = AuthenticationHelper.getAuthenticationResultSource()
        disposable = source.subscribe{
            /**
             * TODO should handle the cases were null is sent from the source observable.
             *  Look [TwitterAuthenticationService]
             */
            when(it){
                is AuthenticationResult.Success -> {
                    userManager.authenticateUser(it.user)
                    navigateToHomeFragment()
                }
                is AuthenticationResult.Failure -> {
                    if(it.throwable is ApiException){
                        showDialog(it.throwable.statusCode)
                    }
                    else{
                        showDialog(it.throwable?.message)
                    }
                }
            }
        }

    }

    private suspend fun googleAuthenticationFlow(){
        GoogleSignInService
            .getInstance(requireActivity(),this)
            .login(null)
    }

    private suspend fun twitterAuthenticationFlow(){
        TwitterAuthenticationService
            .getInstance(requireActivity(),firebaseAuth,authenticationHelper)
            .login(null)
    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest){
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun navigateToHomeFragment() {
        lifecycleScope.launch {
            coordinator.postAction(AuthenticationActions.ToHomeScreen)
        }
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
         lifecycleScope.launch(Dispatchers.Main){ buildDialog(title,message).show() }
    }

    private fun buildDialog(title: String,message: String?) : MaterialAlertDialogBuilder{
        val defaultMessage = getString(R.string.generic_authentication_error_message)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message ?: defaultMessage)
            .setNeutralButton(getString(R.string.ok)) { _, _ -> /* Do nothing*/ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onDestroy() {
        super.onDestroy()
        authenticationHelper.disposeAuthenticationResult(disposable)
    }
}