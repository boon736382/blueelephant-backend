package com.pongsawad.blueelephant.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserData?
)

@Serializable
data class UserData(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    @SerialName("profile_image")
    val profile_image: String? = null
)