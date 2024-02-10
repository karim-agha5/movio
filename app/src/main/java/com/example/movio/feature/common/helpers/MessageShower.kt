package com.example.movio.feature.common.helpers

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.movio.R
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Experimental
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * This class is purely for experimental reasons and may be removed in the future without any further notice
 * @TODO add an Opt-in annotation
 * */
object MessageShower {
/*
    private val weakProgressIndicator: WeakReference<IndeterminateDrawable<CircularProgressIndicatorSpec>>
    init {
        val spec = CircularProgressIndicatorSpec(
            MovioApplication.appContext,
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        weakProgressIndicator = WeakReference(IndeterminateDrawable.createCircularDrawable(MovioApplication.appContext, spec))
    }
*/
    private fun getAppContext() = MovioApplication.appContext

     /*fun startGoogleAuthenticationLoading(btn: MaterialButton,strRes: Int){
         btn.icon = weakProgressIndicator.get()
         btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
         btn.text = getAppContext().getText(strRes)
    }

     fun startTwitterAuthenticationLoading(btn: MaterialButton,strRes: Int){
         btn.icon = weakProgressIndicator.get()
         btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
         btn.text = getAppContext().getText(strRes)
    }

     fun stopGoogleAuthenticationLoading(btn: MaterialButton,strRes: Int){
        btn.icon = ResourcesCompat.getDrawable(getAppContext().resources,
            R.drawable.google_circular_icon,getAppContext().theme)
        btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        btn.text = getAppContext().getText(strRes)
    }

     fun stopTwitterAuthenticationLoading(btn: MaterialButton,strRes: Int){
        btn.icon = ResourcesCompat.getDrawable(getAppContext().resources,
            R.drawable.twitter_icon,getAppContext().theme)
        btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        btn.text = getAppContext().getText(strRes)
    }*/

    @Experimental
    fun showAppropriateErrorDialog(context: Context, throwable: Throwable?){
        if(throwable is ApiException) showDialog(context,throwable.statusCode)
        else showDialog(context, throwable?.message)
    }

    private fun showDialog(context: Context, message: String?){
            buildDialog(
                context,
                getAppContext().getText(R.string.generic_authentication_error_title).toString(),
                message
            ).show()
    }

    private fun showDialog(context: Context, statusCode: Int){
        val title: String
        val message: String
        when(statusCode) {
            CommonStatusCodes.CANCELED  -> {
                title = getAppContext().getText(R.string.canceled_authentication_title).toString()
                message = getAppContext().getText(R.string.canceled_authentication_message).toString()
            }
            CommonStatusCodes.NETWORK_ERROR -> {
                title = getAppContext().getText(R.string.canceled_authentication_title).toString()
                message = getAppContext().getText(R.string.canceled_authentication_message).toString()
            }
            else -> {
                title = getAppContext().getText(R.string.generic_authentication_error_title).toString()
                message = getAppContext().getText(R.string.generic_authentication_error_message).toString()
            }
        }
        // Show the dialog on the main thread
        // because this function is called from an observer on a background thread
        buildDialog(context,title,message).show()
    }

    private fun buildDialog(context: Context, title: String, message: String?) : MaterialAlertDialogBuilder {
        val defaultMessage = getAppContext().getText(R.string.generic_authentication_error_message).toString()
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message ?: defaultMessage)
            .setNeutralButton(getAppContext().getText(R.string.ok)) { _, _ -> /* Do nothing*/ }
    }
}