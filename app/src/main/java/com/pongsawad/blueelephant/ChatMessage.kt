package com.pongsawad.blueelephant

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("sender")
    val sender: String? = "User",

    @SerializedName("sender_email")
    val senderEmail: String,

    @SerializedName("receiver_email")
    val receiverEmail: String? = "", // Default value fixes the red line!

    @SerializedName("content")
    val content: String,

    @SerializedName("created_at")
    val createdAt: String? = null
)