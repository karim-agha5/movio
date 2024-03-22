package com.example.movio.core.helpers

import androidx.fragment.app.Fragment
import com.example.movio.core.MovioApplication
import com.example.movio.core.common.Action
import com.example.movio.core.common.BaseFragment
import com.example.movio.core.common.BaseViewModel
import com.example.movio.core.common.Data
import com.example.movio.core.common.Status
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<D : Data, ActionType : Action, S : Status>(
    movioApplication: MovioApplication,
    private val cls: Class<out Fragment>
) : ReadOnlyProperty<BaseFragment<*,*,*,*>,BaseViewModel<D,ActionType,S>>{

    private val coordinator by CoordinatorDelegate(movioApplication)

    override operator fun getValue(
        thisRef: BaseFragment<*,*,*,*>,
        property: KProperty<*>
    ): BaseViewModel<D,ActionType,S> {
        return  coordinator.requireViewModel(cls)
    }

    operator fun getValue(s: S?, property: KProperty<*>): BaseViewModel<D, ActionType, S> {
        return  coordinator.requireViewModel(cls)
    }
}