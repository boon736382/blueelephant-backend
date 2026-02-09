package com.pongsawad.blueelephant


data class Friend(
    val id: String,    // Make sure this is 'id'
    val name: String,  // Make sure this is 'name'
    val email: String , // Make sure this is 'email'
    val status: String?,         // Make sure your API sends "status"
    val imageUrl: String? // 👈 Make sure this is also here!
)