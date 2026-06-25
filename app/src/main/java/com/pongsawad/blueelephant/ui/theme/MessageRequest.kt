package com.pongsawad.blueelephant.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class MessageRequest(
    @SerialName("sender_email")
    val senderEmail: String,
    @SerialName("receiver_email")
    val receiverEmail: String,
    @SerialName("content")
    val content: String
)