package com.example.movio.feature.authentication.helpers

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.movio.feature.authentication.services.GoogleSignInService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This class is added as a lifecycle observer and registers the launcher that is responsible for
 * handling the result callback for the authentication request and unregistering the launcher
 * when the lifecycle is destroyed
 * */
class AuthenticationLifecycleObserver(
    private val key: String,
    private val registry: ActivityResultRegistry,
    private val googleSignInService: GoogleSignInService
) : DefaultLifecycleObserver {

    lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(owner: LifecycleOwner) {
        launcher = registry.register(
            key,
            owner,
            ActivityResultContracts.StartIntentSenderForResult()
        ){
            owner.lifecycleScope.launch(Dispatchers.Main) {
                googleSignInService.authenticateWithFirebase(it.data)
            }
        }
    }

    fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest){
        launcher.launch(intentSenderRequest)
    }
}