package com.pongsawad.blueelephant

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button
    private lateinit var tvTitle: TextView
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private var receiverEmail: String = "" // Person you are talking to

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Get Emails from Prefs and Intent
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val myEmail = prefs.getString("user_email", "") ?: ""
        receiverEmail = intent.getStringExtra("RECEIVER_EMAIL") ?: ""
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Chat"

        // 2. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)
        tvTitle.text = friendName

        // 3. Setup Adapter (FIXED: Pass myEmail as the second argument)
        adapter = ChatAdapter(messageList, myEmail)
        chatRecycler.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Keeps chat at the bottom
        }
        chatRecycler.adapter = adapter

        sendBtn.setOnClickListener {
            sendMessage(myEmail)
        }
    }

    private fun sendMessage(myEmail: String) {
        val text = inputMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            // FIXED: Added senderEmail so it's no longer red
            val myMsg = ChatMessage(
                sender = "Me",
                senderEmail = myEmail,
                content = text
            )

            messageList.add(myMsg)
            adapter.notifyItemInserted(messageList.size - 1)
            chatRecycler.scrollToPosition(messageList.size - 1)
            inputMessage.text.clear()

            // Here is where you call ApiClient.apiService.sendMessage(...)
        }
    }
}