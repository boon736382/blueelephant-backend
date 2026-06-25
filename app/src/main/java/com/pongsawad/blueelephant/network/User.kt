package com.pongsawad.blueelephant.network

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String?,
    val email: String?,
    val profile_image: String?,
    val status: String?
)