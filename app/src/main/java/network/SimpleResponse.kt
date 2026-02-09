package com.pongsawad.blueelephant.network

// This class maps the JSON response from your server
data class SimpleResponse(
    val message: String,
    val status: Int? = null,
    val success: Boolean = false
)