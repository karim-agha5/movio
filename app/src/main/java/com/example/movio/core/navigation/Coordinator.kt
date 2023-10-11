package com.example.movio.core.navigation

import androidx.fragment.app.Fragment
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status

interface Coordinator {

    /**
     * Handle actions sent from a fragment.
     * A single method to coordinate views in a flow to simplify the Coordinator API used by the views.
     * */
    suspend fun postAction(action: Action)


    fun <D : Data, A : Action,S : Status> requireViewModel(cls: Class<out Fragment>) : BaseViewModel<D,A,S>
}