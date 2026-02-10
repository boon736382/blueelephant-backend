package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.ChatAdapter
import com.pongsawad.blueelephant.ChatMessage
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.MessageRequest
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button
    private lateinit var tvTitle: TextView
    private val messages = mutableListOf<ChatMessage>()

    private var receiverEmail: String = ""
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Get data from Intent
        receiverEmail = intent.getStringExtra("RECEIVER_EMAIL") ?: ""
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Friend"

        // 2. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)

        tvTitle.text = friendName

        // 3. Setup RecyclerView
        // FIX: You MUST pass your own email here to fix the red line!
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val myEmail = prefs.getString("user_email", "") ?: ""

        adapter = ChatAdapter(messages, myEmail) // Now matches the new constructor
        chatRecycler.adapter = adapter

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager

        // 4. Button Logic
        sendBtn.setOnClickListener {
            sendMessage()
        }

        // 5. Load History
        fetchMessages()
    }

    private fun sendMessage() {
        val text = inputMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val myEmail = prefs.getString("user_email", "") ?: ""
            val myName = prefs.getString("user_name", "Me") ?: "Me"

            // Local UI Update
            val newMessage = ChatMessage(
                sender = myName,
                senderEmail = myEmail,
                content = text
            )
            messages.add(newMessage)
            adapter.notifyItemInserted(messages.size - 1)
            chatRecycler.smoothScrollToPosition(messages.size - 1)
            inputMessage.text.clear()

            // Call your API here to save to PostgreSQL
            lifecycleScope.launch {
                try {
                    ApiClient.apiService.sendMessage(
                        MessageRequest(myEmail, receiverEmail, text)
                    )
                } catch (e: Exception) {
                    Log.e("API_ERROR", e.message ?: "Error")
                }
            }
        }
    }

    private fun fetchMessages() {
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val myEmail = prefs.getString("user_email", "") ?: ""

        lifecycleScope.launch {
            try {
                // 1. Call the API (receiverEmail is the person you clicked on)
                val response = ApiClient.apiService.getMessages(myEmail, receiverEmail)

                if (response.isSuccessful) {
                    val history = response.body() ?: emptyList()

                    // 2. Clear old local list and add the fresh history from DB
                    messages.clear()
                    messages.addAll(history)

                    // 3. Tell the adapter to refresh the UI
                    adapter.notifyDataSetChanged()

                    // 4. Scroll to the most recent message
                    if (messages.isNotEmpty()) {
                        chatRecycler.scrollToPosition(messages.size - 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("FETCH_ERROR", "Failed to load history: ${e.message}")
            }
        }
    }
}
