package com.pongsawad.blueelephant

data class Friend(
    val id: Int,
    val name: String, // This MUST match the JSON key exactly
    val email: String? = null
)