package com.example.movio.feature.authentication.signup.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.movio.MainActivity
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.Experimental
import com.example.movio.core.helpers.Event
import com.example.movio.core.helpers.ViewModelDelegate
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.core.util.FormUtils
import com.example.movio.databinding.FragmentSignupBinding
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.common.models.SignupCredentials
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.services.TwitterAuthenticationService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.signup.status.SignupStatus
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.MessageShower
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.splash.viewmodel.SplashViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignupFragment :
    BaseFragment<FragmentSignupBinding>(),
    AuthenticationResultCallbackLauncher,
    LifecycleEventObserver {


    private val coordinator by lazy {
        movioApplication
            .movioContainer
            .rootCoordinator
            .requireCoordinator()
    }
    //private val authenticationHelper by lazy { movioApplication.movioContainer.authenticationHelper }
    /*
   *  TODO find another way to get a view model other than the coordinator.
   *   the coordinator is supposed to be only inside the view model.
   *   the view should be agnostic of anything except its state
   * */
    /*private val signupViewModel by lazy {
        coordinator
            .requireViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>(this::class.java)
        as FederatedAuthenticationBaseViewModel
    }*/
    private lateinit var signupViewModel: FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>
    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var credentialsProgressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    //private lateinit var disposable: Disposable


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val vm by ViewModelDelegate<SignupCredentials, SignupActions, Event<SignupStatus>>(movioApplication,this::class.java)
        //signupViewModel = vm as FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>
        //googleSignInService = GoogleSignInService.getInstance(requireActivity(),this)
        //signupViewModel.register(requireActivity())
        //signupViewModel.register(this)
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(
                this::class.java.simpleName,
                requireActivity().activityResultRegistry,
                signupViewModel.getGoogleSignInService()
            )
        lifecycle.addObserver(authenticationLifecycleObserver)
        lifecycle.addObserver(signupViewModel)
        prepareAuthenticationLoading()
        prepareCredentialsAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume: ")
        //signupViewModel.register(requireActivity())
        signupViewModel.register(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignupBinding {
        return FragmentSignupBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
/*
        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/}
        binding.btnGoogle.setOnClickListener { lifecycleScope.launch { startGoogleAuthenticationFlow() } }
        binding.btnTwitter.setOnClickListener { lifecycleScope.launch { startTwitterAuthenticationFlow() } }
        binding.tvSignIn.setOnClickListener { navigateToSignInScreen() }
        binding.btnSignup.setOnClickListener {
            if(areFieldsValid()){
                Utils.hideKeyboard(requireActivity())
                lifecycleScope.launch { signUpUsingEmailAndPassword() }
            }
            else{
                setTextInputLayoutErrorStyling()
            }
        }
*/

        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/}
        binding.btnGoogle.setOnClickListener {
            startGoogleAuthenticationLoading()
            signupViewModel.postAction(null,SignupActions.GoogleClicked)
        }
        binding.btnTwitter.setOnClickListener {
            startTwitterAuthenticationLoading()
            signupViewModel.postAction(null,SignupActions.TwitterClicked)
        }
        binding.tvSignIn.setOnClickListener { signupViewModel.postAction(null,SignupActions.SignInClicked) }
        binding.btnSignup.setOnClickListener {
            if(areFieldsValid()){
                startCredentialsAuthenticationLoading()
                signupViewModel.postAction(
                    SignupCredentials(binding.etEmail.text.toString(),binding.etPassword.text.toString()),
                    SignupActions.SignupClicked
                )
            }
        }


        signupViewModel.result.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let{ status -> onResultReceived(status)}
        }

/*
        val source = authenticationHelper.getAuthenticationResultObservableSource()
        disposable = source.subscribe{ onAuthenticationResultReceived(it) }
 */

    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest) {
        Log.i("MainActivity", "inside launchAuthenticationResultCallbackLauncher")
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
        //(requireActivity() as AuthenticationResultCallbackLauncher).launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun onResultReceived(signupStatus: SignupStatus){
        when(signupStatus){
            is SignupStatus.ShouldVerifyEmail -> {
                stopCredentialsAuthenticationLoading()
                showShouldVerifyEmailToast()
            }
            is SignupStatus.SignupFailed -> onSignUpFailure(signupStatus.throwable)
            else -> {/*Do Nothing*/ }
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
        binding.btnSignup.icon = credentialsProgressIndicatorDrawable
        binding.btnSignup.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        binding.btnSignup.text = getString(R.string.attempting_to_sign_up_message)
    }

    private fun stopCredentialsAuthenticationLoading(){
        binding.btnSignup.text = getString(R.string.btn_sign_up_text)
        binding.btnSignup.icon = null
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

    private fun onSignUpFailure(throwable: Throwable?){
        Log.i("MainActivity", "Inside onSignUpFailure -> ${throwable?.message}\n\n ${throwable?.toString()}")
        throwable?.printStackTrace()
        //showAppropriateDialog(throwable)
        @OptIn(Experimental::class)
        MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
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

    private fun signUpUsingEmailAndPassword(){
        val credentials =
            SignupCredentials(binding.etEmail.text.toString(),binding.etPassword.text.toString())
        signupViewModel.postAction(credentials, SignupActions.SignupClicked)
    }
*/
    private fun onEmailVerificationStatusReceived(signInStatus: SignInStatus){
        when(signInStatus){
            is SignInStatus.ShouldVerifyEmail -> showShouldVerifyEmailToast()
            else -> {/* Do Nothing */}
        }
    }

    /*private fun onAuthenticationResultReceived(authenticationResult: AuthenticationResult){
        *//**
         * TODO should handle the cases were null is sent from the source observable.
         *  Look [TwitterAuthenticationService]
         *//*
        when(authenticationResult){
            is AuthenticationResult.Success -> {
                // onSuccessfulAuthentication(authenticationResult.user)
            }
            is AuthenticationResult.Failure -> showAppropriateDialog(authenticationResult.throwable)
        }
    }*/

   /* private fun onSuccessfulAuthentication(firebaseUser: FirebaseUser?){
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

    private fun navigateToSignInScreen(){
        lifecycleScope.launch { coordinator.postAction(AuthenticationActions.ToSignInScreen) }
    }
*/

       /* private fun showAppropriateDialog(throwable: Throwable?) =
       when (throwable) {
           is ApiException                          -> {
               // showDialog(throwable.statusCode)
               @OptIn(Experimental::class)
               MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
           }
           is FirebaseAuthUserCollisionException    -> {
               //showDialog(throwable)
               @OptIn(Experimental::class)
               MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
           }
           else                                     -> {
               //showDialog(throwable?.message)
               @OptIn(Experimental::class)
               MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
           }
       }*/
/*
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

    private fun showDialog(throwable: Throwable?){
        lifecycleScope.launch(Dispatchers.Main) {
            //buildDialog("Error","This user has already signed up before").show()
            buildDialog("Error",throwable?.message).show()
        }
    }

    private fun buildDialog(title: String,message: String?) : MaterialAlertDialogBuilder {
        val defaultMessage = getString(R.string.generic_authentication_error_message)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message ?: defaultMessage)
            .setNeutralButton(getString(R.string.ok)) { _, _ -> /* Do nothing*/ }
    }
*/
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

    private fun showShouldVerifyEmailToast(){
        Toast.makeText(requireContext(), resources.getString(R.string.should_verify_email), Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        //AuthenticationHelper.disposeAuthenticationResult(disposable)
        // TODO maybe change the state when the user is actually authenticated, not when the fragment is destroyed
        //lifecycleScope.launch { coordinator.postAction(StateActions.ToAuthenticated) }
        //signupViewModel.unregister()
    }

    /**
     * Necessary for the [SignupViewModel] instantiation.
     * The instantiation of the [SignupViewModel] requires the [MainActivity] onCreate() lifecycle
     * callback to be called first so that it initializes the [RootCoordinator] state correctly.
     * Therefore, an observation on the [MainActivity] lifecycle is necessary.
     * The observation on the [MainActivity] lifecycle is registered in the [onAttach] lifecycle callback.
     * */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_CREATE){
            val vm by ViewModelDelegate<SignupCredentials, SignupActions, Event<SignupStatus>>(movioApplication,this::class.java)
            signupViewModel = vm as FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>
            signupViewModel.register(requireActivity())
        }
    }
}