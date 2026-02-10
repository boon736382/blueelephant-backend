package com.pongsawad.blueelephant.network

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserData // This must match the object we sent from Node.js
)

