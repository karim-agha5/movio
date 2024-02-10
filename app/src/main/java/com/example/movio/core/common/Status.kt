package com.example.movio.core.common

/**
 * Marker interface to mark instances to be of type Status.
 *
 * Necessary for the ViewModels type parameters. As each ViewModel needs to provide the views associated with it
 * with their current status/state.
 * (e.g. Should the view at this point of time should be in the loading status, successful operation status, etc)
 *
 * TODO replace marker interfaces with annotations.
 * */
interface Status {
}