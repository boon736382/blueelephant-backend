package com.pongsawad.blueelephant.network

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)
