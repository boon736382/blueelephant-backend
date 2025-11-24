package com.pongsawad.blueelephant.network

data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)
