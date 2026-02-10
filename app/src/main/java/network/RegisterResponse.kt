package com.pongsawad.blueelephant.network

// Inside RegisterResponse.kt

data class RegisterResponse(
    val message: String,
    val token: String?,
    val user: UserData?  // <--- This line is likely missing!
)
