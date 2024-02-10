package com.example.movio.core.common

/**
 * Marker interface to so onEvent() can receive an implementation of the Event interface instead of Any.
 * Marker interface so that all sealed classes and their subclasses that represents an action on the screen
 * are marked as Action.
 *
 * Necessary for the ViewModels type parameters. As each ViewModel needs an event of type Action
 *
 * TODO make sure to replace this and every marker interface, maybe with annotations ????
 * */
interface Action{}