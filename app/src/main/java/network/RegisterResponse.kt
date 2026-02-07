package com.pongsawad.blueelephant.network

data class RegisterResponse(
    val success: Boolean,
    val message: String?,
    val user: UserResponse?
)

data class UserResponse(
    val id: String?,
    val name: String?,
    val email: String?
)
