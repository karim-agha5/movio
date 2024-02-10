package com.example.movio.feature.authentication.helpers

import com.example.movio.core.common.Data

abstract class BaseCredentials(
    open val email: String,
    open val password: String
) : Data
