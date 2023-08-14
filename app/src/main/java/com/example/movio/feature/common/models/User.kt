package com.example.movio.feature.common.models

import android.graphics.Bitmap

data class User(
    val userName: String,
    val email: String,
    val profilePicture: Bitmap?
)
