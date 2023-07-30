package com.example.movio.feature.authentication.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.movio.MainActivity
import com.example.movio.R
import com.example.movio.databinding.FragmentAuthenticationBinding
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.helpers.AuthenticationLifecycleObserver
import com.example.movio.feature.authentication.helpers.AuthenticationResult
import com.example.movio.feature.authentication.helpers.AuthenticationResultCallbackLauncher
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxjava3.disposables.Disposable
import java.util.Arrays

class AuthenticationFragment : Fragment(),AuthenticationResultCallbackLauncher {

    private lateinit var binding: FragmentAuthenticationBinding
    private lateinit var googleSignInService: GoogleSignInService
    private lateinit var authenticationLifecycleObserver: AuthenticationLifecycleObserver
    private lateinit var disposable: Disposable
    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInService = GoogleSignInService.getInstance(requireActivity(),this)
        // Register the authentication lifecycle observer
        // to unregister the launcher when the Lifecycle is destroyed
        authenticationLifecycleObserver =
            AuthenticationLifecycleObserver(requireActivity().activityResultRegistry,googleSignInService)
        lifecycle.addObserver(authenticationLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_authentication,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.btnContinueWithFacebook.setOnClickListener {
            //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        }


        binding.btnContinueWithGoogle.setOnClickListener { sign() }

        val source = AuthenticationHelper.getAuthenticationResultSource()
        disposable = source.subscribe{
            when(it){
                is AuthenticationResult.Success -> {
                    Log.i(tag, "Received the account inside the fragment | ${it.user}")
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

    private fun sign(){
        val googleService = GoogleSignInService.getInstance(requireActivity(),this)
        googleService.login(null)
    }

    override fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest){
        authenticationLifecycleObserver.launchAuthenticationResultCallbackLauncher(intentSenderRequest)
    }

    private fun showDialog(message: String?){
        buildDialog(
            getString(R.string.generic_authentication_error_title),
            message
        ).show()
    }
    private fun showDialog(statusCode: Int){
        val title: String
        val message: String
        when(statusCode) {
            CommonStatusCodes.CANCELED -> {
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
         buildDialog(title,message).show()
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
        AuthenticationHelper.disposeAuthenticationResult(disposable)
    }
}