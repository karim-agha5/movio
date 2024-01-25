package com.example.movio.core.common

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(message = "This class is experimental and " +
        "it may be changed in the future without any further notice.")
annotation class Experimental