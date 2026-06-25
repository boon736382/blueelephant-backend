package com.pongsawad.blueelephant

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.network.ApiClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button
    private lateinit var tvTitle: TextView
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private var receiverEmail: String = "" // Person you are talking to
    private var myEmail: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private val pollRunnable = object : Runnable {
        override fun run() {
            fetchMessages()
            handler.postDelayed(this, 3000) // Poll every 3 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Get Emails from Prefs and Intent
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        myEmail = prefs.getString("user_email", "") ?: ""
        receiverEmail = intent.getStringExtra("RECEIVER_EMAIL") ?: ""
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Chat"

        // 2. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)
        tvTitle.text = friendName

        // 3. Setup Adapter
        adapter = ChatAdapter(messageList, myEmail)
        chatRecycler.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Keeps chat at the bottom
        }
        chatRecycler.adapter = adapter

        sendBtn.setOnClickListener {
            sendMessage()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(pollRunnable) // Start polling when screen is visible
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(pollRunnable) // Stop polling when user leaves
    }

    private fun fetchMessages() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch messages from Supabase PostgreSQL 'messages' table
                val newMessages = ApiClient.supabase.postgrest["messages"]
                    .select {
                        filter {
                            or {
                                and {
                                    eq("sender_email", myEmail)
                                    eq("receiver_email", receiverEmail)
                                }
                                and {
                                    eq("sender_email", receiverEmail)
                                    eq("receiver_email", myEmail)
                                }
                            }
                        }
                    }.decodeList<ChatMessage>()

                withContext(Dispatchers.Main) {
                    // Only update UI if the message count changed
                    if (newMessages.size != messageList.size) {
                        messageList.clear()
                        messageList.addAll(newMessages)
                        adapter.notifyDataSetChanged()
                        chatRecycler.scrollToPosition(messageList.size - 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatRoom", "Error fetching messages", e)
            }
        }
    }

    private fun sendMessage() {
        val text = inputMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            val newMessage = ChatMessage(
                senderEmail = myEmail,
                receiverEmail = receiverEmail,
                content = text
            )

            // Clear input immediately for better UX
            inputMessage.text.clear()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Insert message into Supabase PostgreSQL 'messages' table
                    ApiClient.supabase.postgrest["messages"].insert(newMessage)
                    fetchMessages() // Immediately refresh
                } catch (e: Exception) {
                    Log.e("ChatRoom", "Error sending message", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ChatRoomActivity, "Network Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
