package com.example.movio.core.helpers

import com.example.movio.core.MovioApplication
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.navigation.Coordinator
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CoordinatorDelegate(
    private val movioApplication: MovioApplication
) : ReadOnlyProperty<BaseViewModel<*,*,*>,Coordinator>{
    /*override operator fun getValue(thisRef: BaseFragment<*>, property: KProperty<*>) =
        movioApplication
            .movioContainer
            .rootCoordinator
            .requireCoordinator()*/

    override fun getValue(thisRef: BaseViewModel<*, *, *>, property: KProperty<*>): Coordinator {
        return movioApplication
            .movioContainer
            .rootCoordinator
            .requireCoordinator()
    }

    operator fun getValue(thisRef: ViewModelDelegate<*, *, *>, property: KProperty<*>): Coordinator {
        return movioApplication
            .movioContainer
            .rootCoordinator
            .requireCoordinator()
    }

}

/*
class LazyCoordinatorDelegate(private val movioApplication: MovioApplication) :
    ReadOnlyProperty<BaseFragment<*>, Coordinator> {

    private var coordinator: Coordinator? = null

    override fun getValue(thisRef: BaseFragment<*>, property: KProperty<*>): Coordinator {
        if (coordinator == null) {
            coordinator = CoordinatorDelegate(movioApplication).getValue(thisRef, property)
        }
        return coordinator!!
    }
}*/
