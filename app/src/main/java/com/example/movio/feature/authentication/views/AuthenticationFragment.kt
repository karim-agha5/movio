package com.example.movio.feature.authentication.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.movio.R
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.Experimental
import com.example.movio.core.helpers.CoordinatorDelegate
import com.example.movio.core.helpers.Event
import com.example.movio.core.navigation.Coordinator
import com.example.movio.core.navigation.CoordinatorHost
import com.example.movio.databinding.FragmentAuthenticationBinding
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.helpers.FederatedAuthenticationBaseViewModel
import com.example.movio.feature.common.models.LoginCredentials
import com.example.movio.feature.authentication.signin.actions.SignInActions
import com.example.movio.feature.authentication.status.SignInStatus
import com.example.movio.feature.common.helpers.MessageShower
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlin.reflect.KProperty

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


    //private val coordinatorDelegate by lazy { LazyCoordinatorDelegate(movioApplication) }

    /*override val coordinator: Coordinator by lazy {
        CoordinatorDelegate(movioApplication)
    }*/

    private val authenticationViewModel by lazy {
        coordinator
            .requireViewModel<LoginCredentials,SignInActions, Event<SignInStatus>>(this::class.java)
        as FederatedAuthenticationBaseViewModel
    }
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var alertDialog: AlertDialog
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationViewModel.register(requireActivity())
        //authenticationViewModel.register(this)
        // Register the authentication lifecycle observer
        // to unregister the launcher when the Lifecycle is destroyed.
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(this::class.java.simpleName,requireActivity().activityResultRegistry,authenticationViewModel.getGoogleSignInService())
        lifecycle.addObserver(authenticationLifecycleObserver)
        lifecycle.addObserver(authenticationViewModel)
        prepareAuthenticationLoading()
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "onResume: ")
        //authenticationViewModel.register(requireActivity())
        authenticationViewModel.register(this)
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
                coordinator.postAction(AuthenticationActions.ToSignupScreen)
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






        //prepareAuthenticationLoadingDialog()

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


    private fun onResultReceived(signInStatus: SignInStatus) =
        when(signInStatus){
            is SignInStatus.SignInFailed    -> {
                @OptIn(Experimental::class)
                MessageShower.showAppropriateErrorDialog(requireContext(),signInStatus.throwable)
                Log.i("MainActivity", "The throwing class is -> ${signInStatus.throwable?.stackTrace?.get(0)?.className} \n " +
                        " ${signInStatus.throwable?.stackTrace?.get(1)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(2)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(3)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(4)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(5)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(6)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(7)?.className} \n"
                        +
                        " ${signInStatus.throwable?.stackTrace?.get(8)?.className} \n" +
                        " ${signInStatus.throwable?.stackTrace?.get(9)?.methodName} \n"

                )
                stopGoogleAuthenticationLoading()
                stopTwitterAuthenticationLoading()
            }
            else                            -> { /*Do Nothing*/ }
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

    //private fun showAuthenticationLoadingDialog() = buildSignInLoadingDialog().show()

    //private fun dismissAuthenticationLoadingDialog() = alertDialog.dismiss()

    //private fun prepareAuthenticationLoadingDialog() = setAuthenticationLoadingDialogToBeDismissedLater(buildSignInLoadingDialog())

/*
    private fun setAuthenticationLoadingDialogToBeDismissedLater(materialAlertDialogBuilder: MaterialAlertDialogBuilder) {
        alertDialog = materialAlertDialogBuilder.create()
    }

    private fun showAppropriateErrorDialog(throwable: Throwable?){
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

    private fun buildSignInLoadingDialog() =
         MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.loading_dialog_layout)
            .setMessage(R.string.attempting_to_sign_in_message)
             .setNegativeButton("Cancel"){_,_ ->
                 dismissAuthenticationLoadingDialog()
             }
            .setCancelable(false)


    private fun buildSignupLoadingDialog()  =
        MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.loading_dialog_layout)
            .setMessage(R.string.hold_on)
            .setCancelable(false)
*/
    override fun onDestroy() {
        super.onDestroy()
        //authenticationViewModel.unregister()
        Log.i("MainActivity", "onDestroy: ")
        //authenticationHelper.disposeAuthenticationResult(disposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //authenticationLifecycleObserver.launcher.unregister()
    }
    override fun onPause() {
        super.onPause()
        //authenticationLifecycleObserver.launcher.unregister()
    }
}




//operator fun <CoordinatorDelegate> Lazy<CoordinatorDelegate>.getValue(thisRef: BaseFragment<*>, property: KProperty<*>) : CoordinatorDelegate = value