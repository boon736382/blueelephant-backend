package com.pongsawad.blueelephant


data class Friend(
    val id: String,    // Make sure this is 'id'
    val name: String,  // Make sure this is 'name'
    val email: String , // Make sure this is 'email'
    val status: String = "Offline", // Added status property
    val imageUrl: String? // 👈 Make sure this is also here!
)