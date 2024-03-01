package com.example.movio.feature.authentication.signin.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.movio.MainActivity
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.Experimental
import com.example.movio.core.helpers.Event
import com.example.movio.core.helpers.ViewModelDelegate
import com.example.movio.core.navigation.RootCoordinator
import com.example.movio.core.util.FormUtils
import com.example.movio.core.util.Utils
import com.example.movio.databinding.FragmentSignInBinding
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.actions.AuthenticationActions
import com.example.movio.feature.common.helpers.MessageShower
import com.example.movio.feature.common.status.UserAuthenticationStatus
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.viewmodels.FieldValidationViewModel
import com.example.movio.feature.common.viewmodels.FieldValidationViewModelFactory
import com.example.movio.feature.splash.viewmodel.SplashViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SignInFragment :
    BaseFragment<FragmentSignInBinding>(),
    AuthenticationResultCallbackLauncher,
    LifecycleEventObserver{

    private lateinit var signInViewModel: FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>
    private val fieldValidationViewModel by lazy {
        FieldValidationViewModelFactory(
            movioApplication.movioContainer.validateEmail,
            movioApplication.movioContainer.validatePassword
        ).create(FieldValidationViewModel::class.java)
    }
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var credentialsProgressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val vm by ViewModelDelegate<LoginCredentials, SignInActions, Event<SignInStatus>>(movioApplication,this::class.java)
        signInViewModel = vm as FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>
        signInViewModel.register(requireActivity())
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,signInViewModel.getGoogleSignInService())
        lifecycle.addObserver(authenticationLifecycleObserver)
        lifecycle.addObserver(signInViewModel)
        prepareAuthenticationLoading()
        prepareCredentialsAuthenticationLoading()*/
    }

    override fun onResume() {
        super.onResume()
        signInViewModel.register(this)
    }

    /**
     * Necessary for the [SignInFragment] instantiation.
     * The instantiation of the [SignInFragment] requires the [MainActivity] onCreate() lifecycle
     * callback to be called first so that it initializes the [RootCoordinator] state correctly.
     * Therefore, an observation on the [MainActivity] lifecycle is necessary.
     * The observation on the [MainActivity] lifecycle is registered in the [onAttach] lifecycle callback.
     * */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            val vm by ViewModelDelegate<LoginCredentials, SignInActions, Event<SignInStatus>>(
                movioApplication,
                this::class.java
            )
            signInViewModel = vm as FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>

            signInViewModel.register(requireActivity())
            authenticationLifecycleObserver =
                AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,signInViewModel.getGoogleSignInService())
            lifecycle.addObserver(authenticationLifecycleObserver)
            lifecycle.addObserver(signInViewModel)
            // TODO consider adding those 2 methods in the appropriate callback to make sure the context is initialized
            prepareAuthenticationLoading()
            prepareCredentialsAuthenticationLoading()
        }
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
            fieldValidationViewModel.validate(
                binding.etEmail.text.toString()
                ,binding.etPassword.text.toString()
            )
        }

        signInViewModel.result.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { status -> onResultReceived(status) }
        }

        lifecycleScope.launch {
            fieldValidationViewModel.fieldsState.collect{
                onValidationResultReceived(it)
            }
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

    private fun onValidationResultReceived(result: Triple<ValidationResultState,ValidationResultState,Boolean>){
        if(result.third){
            resetFormTextInputLayoutErrorStyling()
            startCredentialsAuthenticationLoading()
            signInViewModel.postAction(
                LoginCredentials(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ),
                SignInActions.SignInClicked
            )
        }else{
            displayFieldErrorStylingAppropriately(result)
        }
    }

    private fun displayFieldErrorStylingAppropriately(result: Triple<ValidationResultState,ValidationResultState,Boolean>){
        if(result.first is ValidationResultState.Failure){
            setEmailFieldStyling((result.first as ValidationResultState.Failure).type.errorMessage)
        }
        if(result.second is ValidationResultState.Failure){
            setPasswordFieldStyling((result.second as ValidationResultState.Failure).type.errorMessage)
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
        @OptIn(Experimental::class)
        MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
        stopGoogleAuthenticationLoading()
        stopTwitterAuthenticationLoading()
        stopCredentialsAuthenticationLoading()
    }

    private fun isEmailFieldValid() : Boolean {
        return FormUtils.isEmailFieldValid(binding.etEmail.text.toString())
    }

    private fun isPasswordFieldValid() : Boolean {
        return FormUtils.isPasswordFieldValid(binding.etPassword)
    }

    private fun setEmailFieldStyling(errorMessage: String){
        val context = requireContext()
        val tilEmail = binding.tilEmail

        if(isEmailFieldValid()){
            FormUtils.resetTextInputLayoutStyling(tilEmail)
        }
        else{
            FormUtils.setTextInputLayoutErrorStyling(
                context,
                tilEmail,
                errorMessage
            )
        }

    }

    private fun setPasswordFieldStyling(errorMessage: String) {
        val context = requireContext()
        val tilPassword = binding.tilPassword

        if (isPasswordFieldValid()) {
            FormUtils.resetTextInputLayoutStyling(tilPassword)
        }
        else {
            FormUtils.setTextInputLayoutErrorStyling(
                context,
                tilPassword,
                errorMessage
            )
        }

    }

    private fun resetFormTextInputLayoutErrorStyling(){
        resetEmailTextInputLayoutErrorStyling()
        resetPasswordTextInputLayoutErrorStyling()
    }

    private fun resetEmailTextInputLayoutErrorStyling() =
        FormUtils.resetTextInputLayoutStyling(binding.tilEmail)


    private fun resetPasswordTextInputLayoutErrorStyling() =
        FormUtils.resetTextInputLayoutStyling(binding.tilPassword)

    private fun showEmailNotVerifiedToast(){
        Toast.makeText(requireContext(), resources.getString(R.string.email_not_verified), Toast.LENGTH_LONG).show()
    }
}