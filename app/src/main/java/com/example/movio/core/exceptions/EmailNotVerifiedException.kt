package com.example.movio.core.exceptions

class EmailNotVerifiedException : Exception() {
    override val message = "your email is not verified"
}