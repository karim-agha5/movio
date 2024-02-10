package com.example.movio.core.navigation

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import com.example.movio.core.helpers.ViewModelsFactoryProvider
import com.example.movio.feature.authentication.helpers.AuthenticationHelper
import com.example.movio.feature.authentication.navigation.AuthenticationFlowState
import com.example.movio.feature.authentication.navigation.viewmodelsfactory.AuthenticationViewModelsFactory
import com.example.movio.feature.authentication.services.GoogleSignInService
import com.example.movio.feature.common.helpers.UserManager
import com.google.firebase.auth.FirebaseAuth

// TODO this class doesn't follow the SRP. Refactor later.
class RootCoordinator(
    private val application: Application,
    private val viewModelsFactoryProvider: ViewModelsFactoryProvider,
    private val firebaseAuth: FirebaseAuth,
    private val authenticationHelper: AuthenticationHelper,
    private val userManager: UserManager
    ) : FlowContext {

    /**
     * Each coordinator object has a nullable and non-nullable reference in order to dispose the object
     * when its flow is completed and to avoid continuously checking for nullability.
     * */
    private var _navController: NavController? = null
    private val navController get() = _navController!!
    private lateinit var state: FlowState

    /**
     * Must be called when the MainActivity's onCreate is called.
     * */
    fun init(navController: NavController){
        this._navController = navController
        initState()
    }

    private fun initState(){
        // TODO add logic to determine the state of the coordinator
        // Should the flow starts as logged/signed before
        //, should the flow start from authentication
        // , or should the flor start as if the app is newly installed.

        // TODO find a way to retrieve both singletons from the container
        state = AuthenticationFlowState(
            this,
            viewModelsFactoryProvider
                .provideAuthenticationViewModelsFactory(application)
        )
    }

    fun requireCoordinator() : Coordinator {
        if(_navController == null){/*TODO throw a custom exception*/}

        // The current state is responsible for returning the appropriate coordinator
        return state.requireCoordinator(navController)
    }

    fun <D : Data,A : Action,S : Status>
            requireViewModel(cls: Class<out Fragment>) : BaseViewModel<D,A,S> {
        return state.requireViewModel(cls)
    }

    override fun changeState(flowState: FlowState) {
        state = flowState
    }
}