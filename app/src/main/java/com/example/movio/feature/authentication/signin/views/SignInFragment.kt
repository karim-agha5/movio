package com.example.movio.feature.authentication.signin.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.res.ResourcesCompat
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.Experimental
import com.example.movio.core.helpers.Event
import com.example.movio.core.helpers.ViewModelDelegate
import com.example.movio.core.util.FormUtils
import com.example.movio.core.util.Utils
import com.example.movio.databinding.FragmentSignInBinding
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.MessageShower
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable


class SignInFragment :
    BaseFragment<FragmentSignInBinding>(), AuthenticationResultCallbackLauncher {

    private lateinit var signInViewModel: FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>
    private lateinit var credentialsProgressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm by ViewModelDelegate<LoginCredentials, SignInActions, Event<SignInStatus>>(movioApplication,this::class.java)
        signInViewModel = vm as FederatedAuthenticationBaseViewModel<LoginCredentials, SignInActions, Event<SignInStatus>>
        signInViewModel.register(requireActivity())
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,signInViewModel.getGoogleSignInService())
        lifecycle.addObserver(authenticationLifecycleObserver)
        lifecycle.addObserver(signInViewModel)
        prepareAuthenticationLoading()
        prepareCredentialsAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
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
        @OptIn(Experimental::class)
        MessageShower.showAppropriateErrorDialog(requireContext(),throwable)
        stopGoogleAuthenticationLoading()
        stopTwitterAuthenticationLoading()
        stopCredentialsAuthenticationLoading()
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
}