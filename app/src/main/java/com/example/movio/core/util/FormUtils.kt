package com.example.movio.core.util

import android.content.Context
import android.content.res.ColorStateList
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.movio.R
import com.google.android.material.textfield.TextInputLayout

class FormUtils{
    companion object{

        fun setTextInputLayoutErrorStyling(
            context: Context,
            textInputLayout: TextInputLayout,
            errorMessage: String
        ){
            val states = arrayOf(intArrayOf(android.R.attr.stateNotNeeded))
            val colors = intArrayOf(
                ContextCompat.getColor(context, R.color.warning_yellow)
            )
            val colorStateList = ColorStateList(states,colors)
            textInputLayout.boxStrokeWidth = 1
            textInputLayout.boxStrokeErrorColor = colorStateList
            textInputLayout.setErrorIconTintList(colorStateList)
            textInputLayout.error = errorMessage
            textInputLayout.isErrorEnabled = true
        }


        fun setTextInputLayoutErrorStyling(
            context: Context,
            textInputLayout: Array<TextInputLayout>,
            errorMessage: Array<String>
        ){
            val states = arrayOf(intArrayOf(android.R.attr.stateNotNeeded))
            val colors = intArrayOf(
                ContextCompat.getColor(context, R.color.warning_yellow)
            )
            val colorStateList = ColorStateList(states,colors)

            for(i in textInputLayout.indices){
                textInputLayout[i].boxStrokeWidth = 1
                textInputLayout[i].boxStrokeErrorColor = colorStateList
                textInputLayout[i].setErrorIconTintList(colorStateList)
                textInputLayout[i].error = errorMessage[i]
                textInputLayout[i].isErrorEnabled = true
            }

        }

        fun isEmailValid(email: String) : Boolean{
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}