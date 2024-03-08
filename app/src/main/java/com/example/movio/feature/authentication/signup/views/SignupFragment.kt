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
import androidx.lifecycle.lifecycleScope
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
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.common.models.SignupCredentials
import com.example.movio.feature.authentication.signup.actions.SignupActions
import com.example.movio.feature.authentication.signup.status.SignupStatus
import com.example.movio.feature.authentication.signup.viewmodel.SignupViewModel
import com.example.movio.feature.common.helpers.MessageShower
import com.example.movio.feature.common.status.ValidationResultState
import com.example.movio.feature.common.viewmodels.FieldValidationViewModel
import com.example.movio.feature.common.viewmodels.FieldValidationViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlinx.coroutines.launch

class SignupFragment :
    BaseFragment<FragmentSignupBinding>(),
    AuthenticationResultCallbackLauncher,
    LifecycleEventObserver {

    private lateinit var signupViewModel: FederatedAuthenticationBaseViewModel<SignupCredentials, SignupActions, Event<SignupStatus>>
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var credentialsProgressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private val fieldValidationViewModel by lazy{
        FieldValidationViewModelFactory(
            movioApplication.movioContainer.validateEmail,
            movioApplication.movioContainer.validatePassword
        )
            .create(FieldValidationViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareAuthenticationLoading()
        prepareCredentialsAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
        signupViewModel.register(this)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignupBinding {
        return FragmentSignupBinding.inflate(inflater,container,false)
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
            authenticationLifecycleObserver =
                AuthenticationLifecycleObserver(
                    this::class.java.simpleName,
                    requireActivity().activityResultRegistry,
                    signupViewModel::authenticateWithFirebase
                )
            lifecycle.addObserver(authenticationLifecycleObserver)
            lifecycle.addObserver(signupViewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFacebook.setOnClickListener {/*TODO implement when the app is published*/ }
        binding.btnGoogle.setOnClickListener {
            startGoogleAuthenticationLoading()
            signupViewModel.postAction(null, SignupActions.GoogleClicked)
        }
        binding.btnTwitter.setOnClickListener {
            startTwitterAuthenticationLoading()
            signupViewModel.postAction(null, SignupActions.TwitterClicked)
        }
        binding.tvSignIn.setOnClickListener {
            signupViewModel.postAction(
                null,
                SignupActions.SignInClicked
            )
        }
        binding.btnSignup.setOnClickListener {
            fieldValidationViewModel.validate(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }


        signupViewModel.result.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { status ->
                Log.i("MainActivity", "fragment: ${Thread.currentThread().name}")
                onResultReceived(status)
            }
        }

        lifecycleScope.launch {
            fieldValidationViewModel.fieldsState.collect{
                onFieldValidationResultReceived(it)
            }
        }
    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest) {
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun onResultReceived(signupStatus: SignupStatus){
        when(signupStatus){
            is SignupStatus.ShouldVerifyEmail -> {
                Log.i("MainActivity", "Inside should verify block")
                stopCredentialsAuthenticationLoading()
                showShouldVerifyEmailToast()
            }
            is SignupStatus.SignupFailed -> onSignUpFailure(signupStatus.throwable)
            else -> {/*Do Nothing*/ }
        }
    }

    private fun onFieldValidationResultReceived(result: Triple<ValidationResultState,ValidationResultState,Boolean>){
        if(result.third){
            resetFormTextInputLayoutErrorStyling()
            startCredentialsAuthenticationLoading()
            signupViewModel.postAction(
                SignupCredentials(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ),
                SignupActions.SignupClicked
            )
        }
        else{
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
        throwable?.printStackTrace()
        @OptIn(Experimental::class)
        MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
        stopGoogleAuthenticationLoading()
        stopTwitterAuthenticationLoading()
        stopCredentialsAuthenticationLoading()
    }

    private fun setEmailFieldStyling(errorMessage: String) {
        val context = requireContext()
        val tilEmail = binding.tilEmail

        FormUtils.setTextInputLayoutErrorStyling(
            context,
            tilEmail,
            errorMessage
        )
    }

    private fun setPasswordFieldStyling(errorMessage: String) {
        val context = requireContext()
        val tilPassword = binding.tilPassword

        FormUtils.setTextInputLayoutErrorStyling(
            context,
            tilPassword,
            errorMessage
        )
    }

    private fun resetFormTextInputLayoutErrorStyling(){
        resetEmailTextInputLayoutErrorStyling()
        resetPasswordTextInputLayoutErrorStyling()
    }

    private fun resetEmailTextInputLayoutErrorStyling() =
        FormUtils.resetTextInputLayoutStyling(binding.tilEmail)


    private fun resetPasswordTextInputLayoutErrorStyling() =
        FormUtils.resetTextInputLayoutStyling(binding.tilPassword)

    private fun showShouldVerifyEmailToast(){
        Toast.makeText(requireContext(), resources.getString(R.string.should_verify_email), Toast.LENGTH_LONG).show()
    }
}