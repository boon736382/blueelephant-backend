package com.pongsawad.blueelephant

data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)