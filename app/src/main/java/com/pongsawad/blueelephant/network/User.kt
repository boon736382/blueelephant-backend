package com.pongsawad.blueelephant.network

data class User(
    val id: Int,
    val name: String?,
    val email: String?,
    val profile_image: String?,
    val status: String? // This will now receive "Online" or "Offline" from the DB
)