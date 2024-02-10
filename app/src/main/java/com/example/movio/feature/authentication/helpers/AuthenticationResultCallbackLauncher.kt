package com.example.movio.feature.authentication.helpers

import androidx.activity.result.IntentSenderRequest

interface AuthenticationResultCallbackLauncher {
    fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest)
}