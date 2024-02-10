package com.example.movio.core.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

class Utils{
    companion object{
        fun hideKeyboard(activity: Activity){
            val view = activity.currentFocus
            if(view != null){
                val inputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
            }
        }
    }
}