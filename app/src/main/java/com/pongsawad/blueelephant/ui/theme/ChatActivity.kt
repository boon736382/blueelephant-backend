package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.ChatAdapter
import com.pongsawad.blueelephant.ChatMessage
import com.pongsawad.blueelephant.R

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button
    private lateinit var tvTitle: TextView
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)

        // 2. Set the Friend's Name in the Header
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Friend"
        tvTitle.text = friendName

        // 3. Setup RecyclerView
        adapter = ChatAdapter(messages)
        chatRecycler.adapter = adapter

        // Reverse layout is helpful for chats so new messages appear at the bottom
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager

        // 4. Button Logic
        sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val text = inputMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            // Get the current user's name from Prefs
            val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val myName = prefs.getString("user_name", "Me") ?: "Me"

            // Local Update (UI)
            val newMessage = ChatMessage(sender = myName, content = text)
            messages.add(newMessage)

            adapter.notifyItemInserted(messages.size - 1)
            chatRecycler.smoothScrollToPosition(messages.size - 1)
            inputMessage.text.clear()

            // TODO: Call ApiClient.apiService.sendMessage(...) here
            // to save this message to your PostgreSQL database!
        }
    }
}