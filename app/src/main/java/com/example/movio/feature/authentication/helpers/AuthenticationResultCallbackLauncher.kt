package com.example.movio.feature.authentication.helpers

import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest

interface AuthenticationResultCallbackLauncher {
    fun launchAuthenticationResultCallbackLauncher(intentSenderRequest: IntentSenderRequest)
}