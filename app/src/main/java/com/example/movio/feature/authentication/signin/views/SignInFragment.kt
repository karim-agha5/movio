package com.example.movio.feature.authentication.signin.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.helpers.Event
import com.example.movio.core.util.FormUtils
import com.example.movio.core.util.Utils
import com.example.movio.databinding.FragmentSignInBinding
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.LoginCredentials
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
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

    /*
   *  TODO find another way to get a view model other than the coordinator.
   *   the coordinator is supposed to be only inside the view model.
   *   the view should be agnostic of anything except its state
   * */
    private val signInViewModel by lazy {
        coordinator
            .requireViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>(this@SignInFragment::class.java)
        as FederatedAuthenticationBaseViewModel
    }

    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var credentialsProgressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    //private lateinit var disposable: Disposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //googleSignInService = GoogleSignInService.getInstance(requireActivity(),this)
        signInViewModel.register(requireActivity())
        //signInViewModel.register(this)
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,signInViewModel.getGoogleSignInService())
        lifecycle.addObserver(authenticationLifecycleObserver)
        lifecycle.addObserver(signInViewModel)
        prepareAuthenticationLoading()
        prepareCredentialsAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume: ")
        //authenticationViewModel.register(requireActivity())
        //signInViewModel.register(requireActivity())
        signInViewModel.register(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       /* binding.btnGoogle.setOnClickListener { lifecycleScope.launch { startGoogleAuthenticationFlow() } }
        binding.btnTwitter.setOnClickListener { lifecycleScope.launch { startTwitterAuthenticationFlow() } }
        binding.tvSignUp.setOnClickListener {
            lifecycleScope.launch {
               // coordinator.postAction(AuthenticationActions.ToSignupScreen)
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

        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/}
        binding.btnGoogle.setOnClickListener {
            startGoogleAuthenticationLoading()
            signInViewModel.postAction(null,SignInActions.GoogleClicked)
        }
        binding.btnTwitter.setOnClickListener {
            startTwitterAuthenticationLoading()
            signInViewModel.postAction(null,SignInActions.TwitterClicked)
        }
        binding.tvSignUp.setOnClickListener { signInViewModel.postAction(null,SignInActions.SignupClicked) }
        binding.btnSignIn.setOnClickListener {
            if(areFieldsValid()){
                Utils.hideKeyboard(requireActivity())

                val loginCredentials = LoginCredentials(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
                startCredentialsAuthenticationLoading()
                signInViewModel.postAction(loginCredentials,SignInActions.SignInClicked)
            }
            else{
                setTextInputLayoutErrorStyling()
            }
        }

        signInViewModel.result.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { status -> onResultReceived(status) }
        }

    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest) {
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun onResultReceived(result: SignInStatus){
        when(result){
            is SignInStatus.EmailVerified       ->  signInViewModel.onPostResultActionExecuted(SignInActions.SuccessAction)
            is SignInStatus.EmailNotVerified    ->  onUnverifiedEmailLoginAttempt()

            is SignInStatus.SignInFailed        -> onSignInFailure(result.throwable)
            else -> {/*Do Nothing*/}
        }
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

    private fun prepareCredentialsAuthenticationLoading(){
        val spec = CircularProgressIndicatorSpec(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        spec.indicatorColors = intArrayOf(resources.getColor(R.color.white,context?.theme))
        credentialsProgressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(requireContext(), spec)
    }

    private fun startCredentialsAuthenticationLoading(){
        binding.btnSignIn.icon = credentialsProgressIndicatorDrawable
        binding.btnSignIn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnSignIn.text = getString(R.string.attempting_to_sign_in_message)
    }

    private fun stopCredentialsAuthenticationLoading(){
        binding.btnSignIn.text = getString(R.string.btn_sign_up_text)
        binding.btnSignIn.icon = null
    }

    private fun startGoogleAuthenticationLoading(){
        binding.btnGoogle.icon = progressIndicatorDrawable
    }

    private fun startTwitterAuthenticationLoading(){
        binding.btnTwitter.icon = progressIndicatorDrawable
    }

    private fun stopGoogleAuthenticationLoading(){
        binding.btnGoogle.icon = ResourcesCompat.getDrawable(resources,R.drawable.google_circular_icon,context?.theme)
    }

    private fun stopTwitterAuthenticationLoading(){
        binding.btnTwitter.icon = ResourcesCompat.getDrawable(resources,R.drawable.twitter_icon,context?.theme)
    }

    private fun onUnverifiedEmailLoginAttempt(){
        stopCredentialsAuthenticationLoading()
        showEmailNotVerifiedToast()
        signInViewModel.onPostResultActionExecuted(SignInActions.FailureAction)
    }

    private fun onSignInFailure(throwable: Throwable?){
        showAppropriateDialog(throwable)
        stopGoogleAuthenticationLoading()
        stopTwitterAuthenticationLoading()
        stopCredentialsAuthenticationLoading()
    }
/*
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

    private fun signInUsingEmailAndPassword(){
        val credentials = LoginCredentials(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
        signInViewModel.postAction(credentials, SignInActions.SignInClicked)
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

    override fun onPause() {
        super.onPause()
        //signInViewModel.unregister()
    }

    override fun onDestroy() {
        super.onDestroy()
        //signInViewModel.unregister()
        // AuthenticationHelper.disposeAuthenticationResult(disposable)
    }
}