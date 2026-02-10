package com.pongsawad.blueelephant.network

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserData?
)

// Only keep this here if it's NOT defined in RegisterResponse.kt
data class UserData(
    val id: Int,
    val name: String?,
    val email: String?,
    val age: Int?,
    val gender: String?,
    val profile_image: String?
)