package com.example.movio.core.helpers

import com.example.movio.core.common.Status

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * Specifically used to fix repetitive LiveData notification when re-observing it.
 * @see <a href="https://developer.android.com/topic/architecture/ui-layer/events">Android UI official guidelines</a>
 */
@Deprecated(
    message = "Deprecated since 2021 and should be replaced with the latest Android UI guidelines",
    level = DeprecationLevel.WARNING
)
class Event<out T : Status>(private val content: T) : Status{

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * @return the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * @return the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}