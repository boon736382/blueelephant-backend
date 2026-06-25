package com.pongsawad.blueelephant

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ChatMessage(
    @SerialName("sender")
    val sender: String? = "User",

    @SerialName("sender_email")
    val senderEmail: String,

    @SerialName("receiver_email")
    val receiverEmail: String? = "", 

    @SerialName("content")
    val content: String,

    @SerialName("created_at")
    val createdAt: String? = null
)