package com.example.movio.feature.common.helpers

import android.provider.Settings.Global.getString
import androidx.core.content.res.ResourcesCompat
import com.example.movio.R
import com.example.movio.core.MovioApplication
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import java.lang.ref.WeakReference

/**
 * This class is purely for experimental reasons and may be removed in the future without any further notice
 * @TODO add an Opt-in annotation
 * */
object MessageShower {
    private lateinit var weakProgressIndicator: WeakReference<IndeterminateDrawable<CircularProgressIndicatorSpec>>
    init {
        val spec = CircularProgressIndicatorSpec(
            MovioApplication.appContext,
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        weakProgressIndicator = WeakReference(IndeterminateDrawable.createCircularDrawable(MovioApplication.appContext, spec))
    }

     fun startGoogleAuthenticationLoading(btn: MaterialButton,strRes: Int){
         btn.icon = weakProgressIndicator.get()
         btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
         btn.text = MovioApplication.appContext.getText(strRes)
    }

     fun startTwitterAuthenticationLoading(btn: MaterialButton,strRes: Int){
         btn.icon = weakProgressIndicator.get()
         btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
         btn.text = MovioApplication.appContext.getText(strRes)
    }

     fun stopGoogleAuthenticationLoading(btn: MaterialButton,strRes: Int){
        btn.icon = ResourcesCompat.getDrawable(MovioApplication.appContext.resources,
            R.drawable.google_circular_icon,MovioApplication.appContext.theme)
        btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        btn.text = MovioApplication.appContext.getText(strRes)
    }

     fun stopTwitterAuthenticationLoading(btn: MaterialButton,strRes: Int){
        btn.icon = ResourcesCompat.getDrawable(MovioApplication.appContext.resources,
            R.drawable.twitter_icon,MovioApplication.appContext.theme)
        btn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        btn.text = MovioApplication.appContext.getText(strRes)
    }
}