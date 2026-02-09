package com.pongsawad.blueelephant.network

data class RegisterResponse(
    val message: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val profile_image: String? = null // 👈 Add this line here!
)