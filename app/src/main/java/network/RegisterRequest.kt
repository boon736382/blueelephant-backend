package com.pongsawad.blueelephant.network

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
