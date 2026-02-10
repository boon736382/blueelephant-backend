package com.pongsawad.blueelephant // Double check this matches your folder!

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("sender")
    val sender: String,

    @SerializedName("sender_email") // Matches your pgAdmin column
    val senderEmail: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("created_at") // Useful for the 24-hour delete logic
    val timestamp: Long = System.currentTimeMillis()
)