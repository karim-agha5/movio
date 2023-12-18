package com.example.movio.feature.authentication.signin.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.util.FormUtils
import com.example.movio.core.util.Utils
import com.example.movio.databinding.FragmentSignInBinding
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignInFragment :
    BaseFragment<FragmentSignInBinding>(), AuthenticationResultCallbackLauncher {

    private val coordinator by lazy {
        movioApplication
            .movioContainer
            .rootCoordinator
            .requireCoordinator()
    }

    private val signInViewModel by lazy {
        coordinator
            .requireViewModel<LoginCredentials, SignInActions, SignInStatus>(this@SignInFragment::class.java)
    }

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
    ): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/}
       /* binding.btnGoogle.setOnClickListener { lifecycleScope.launch { startGoogleAuthenticationFlow() } }
        binding.btnTwitter.setOnClickListener { lifecycleScope.launch { startTwitterAuthenticationFlow() } }
        binding.tvSignUp.setOnClickListener {
            lifecycleScope.launch {
               // coordinator.postAction(AuthenticationActions.ToEmailAndPasswordScreen)
            }
        }
        binding.btnSignIn.setOnClickListener {
            if(areFieldsValid()){
                Utils.hideKeyboard(requireActivity())
                lifecycleScope.launch { signInUsingEmailAndPassword() }
            }
            else{
                setTextInputLayoutErrorStyling()
            }
        }

        signInViewModel.result.observe(viewLifecycleOwner){
            when(it){
                is SignInStatus.EmailVerified -> { /* Do Nothing */ }
                is SignInStatus.EmailNotVerified -> showEmailNotVerifiedToast()
                else -> {/*Do nothing*/}
            }
        }

        val source = AuthenticationHelper.getAuthenticationResultObservableSource()
        disposable = source.subscribe{
            /**
             * TODO should handle the cases were null is sent from the source observable.
             *  Look [TwitterAuthenticationService]
             */
            when(it){
                is AuthenticationResult.Success -> {
                    // onSuccessfulAuthentication(it.user)
                }
                is AuthenticationResult.Failure -> showAppropriateDialog(it.throwable)
            }
        }

        */


        binding.btnGoogle.setOnClickListener {
            signInViewModel.postAction(null,SignInActions.GoogleClicked)
        }

        binding.btnTwitter.setOnClickListener {
            signInViewModel.postAction(null,SignInActions.TwitterClicked)
        }

        binding.btnSignIn.setOnClickListener {
            if(areFieldsValid()){
                Utils.hideKeyboard(requireActivity())

                val loginCredentials = LoginCredentials(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
                signInViewModel.postAction(loginCredentials,SignInActions.SignInClicked)
            }
            else{
                setTextInputLayoutErrorStyling()
            }
        }

        signInViewModel.result.observe(viewLifecycleOwner){
            onResultReceived(it)
        }


    }

    private fun onResultReceived(result: SignInStatus){
        lifecycleScope.launch {
            when(result){
                is SignInStatus.EmailVerified -> signInViewModel.onPostResultActionExecuted(SignInActions.SuccessAction)
                is SignInStatus.EmailNotVerified -> {
                    showDialog("Your E-mail is not verified yet.")
                    signInViewModel.onPostResultActionExecuted(SignInActions.FailureAction)
                }
                else -> {/*Do Nothing*/}
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
/*
    private fun signInUsingEmailAndPassword(){
        val credentials = LoginCredentials(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
        signInViewModel.postAction(credentials, SignInActions.SignInClicked)
    }
*/
    /*private fun onSuccessfulAuthentication(firebaseUser: FirebaseUser?){
        authenticateUser(firebaseUser)
        navigateToHome()
    }

    private fun authenticateUser(firebaseUser: FirebaseUser?){
        val userManager = UserManager.getInstance(movioApplication.movioContainer.firebaseAuth)
        userManager.authenticateUser(firebaseUser)
    }

    private fun navigateToHome(){
        lifecycleScope.launch { coordinator.postAction(AuthenticationActions.ToHomeScreen) }
    }*/

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

    private fun areFieldsValid() : Boolean{
        return isEmailFieldValid() && isPasswordFieldValid()
    }

    private fun isEmailFieldValid() : Boolean {
        return FormUtils.isEmailFieldValid(binding.etEmail.text.toString())
    }

    private fun isPasswordFieldValid() : Boolean {
        return FormUtils.isPasswordFieldValid(binding.etPassword)
    }

    private fun setEmailFieldStyling(){
        val context = requireContext()
        val tilEmail = binding.tilEmail

        if(isEmailFieldValid()){
            FormUtils.resetTextInputLayoutStyling(tilEmail)
        }
        else{
            FormUtils.setTextInputLayoutErrorStyling(
                context,
                tilEmail,
                resources.getString(R.string.incorrect_email_format)
            )
        }

    }

    private fun setPasswordFieldStyling() {
        val context = requireContext()
        val tilPassword = binding.tilPassword

        if (isPasswordFieldValid()) {
            FormUtils.resetTextInputLayoutStyling(tilPassword)
        }
        else {
            FormUtils.setTextInputLayoutErrorStyling(
                context,
                tilPassword,
                resources.getString(R.string.incorrect_password_format)
            )
        }

    }

    private fun setTextInputLayoutErrorStyling(){
        setEmailFieldStyling()
        setPasswordFieldStyling()
    }

    private fun showEmailNotVerifiedToast(){
        Toast.makeText(requireContext(), resources.getString(R.string.email_not_verified), Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // AuthenticationHelper.disposeAuthenticationResult(disposable)
    }
}