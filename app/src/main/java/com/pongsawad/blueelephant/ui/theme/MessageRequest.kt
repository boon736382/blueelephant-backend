package com.pongsawad.blueelephant.network

data class MessageRequest(
    val senderEmail: String,
    val receiverEmail: String,
    val content: String
)