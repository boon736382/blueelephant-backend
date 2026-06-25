package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.BaseActivity
import com.pongsawad.blueelephant.ChatAdapter
import com.pongsawad.blueelephant.ChatMessage
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

// Inherit from BaseActivity to enable the language engine
class ChatActivity : BaseActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button
    private lateinit var tvTitle: TextView
    private lateinit var btnTranslate: Button
    private lateinit var btnBack: android.widget.ImageButton

    private val messages = mutableListOf<ChatMessage>()
    private var receiverEmail: String = ""
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Get Intent Data
        receiverEmail = intent.getStringExtra("RECEIVER_EMAIL") ?: ""
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Friend"

        // 2. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)
        btnTranslate = findViewById(R.id.btnTranslate)
        btnBack = findViewById(R.id.btnBack)

        tvTitle.text = friendName

        // 3. Setup RecyclerView
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val myEmail = prefs.getString("user_email", "") ?: ""

        adapter = ChatAdapter(messages, myEmail)
        chatRecycler.adapter = adapter
        chatRecycler.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        // 4. Listeners
        sendBtn.setOnClickListener { sendMessage() }

        btnTranslate.setOnClickListener { toggleLanguage() }

        btnBack.setOnClickListener { finish() }

        // 5. Load Data
        fetchMessages()
    }

    private fun toggleLanguage() {
        val prefs = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("MY_LANG", "en")
        val newLang = if (currentLang == "en") "th" else "en"

        // Save selection
        prefs.edit().putString("MY_LANG", newLang).apply()

        // Restart with a smooth fade animation
        finish()
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun sendMessage() {
        val text = inputMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
            val myEmail = prefs.getString("user_email", "") ?: ""
            val myName = prefs.getString("user_name", "Me") ?: "Me"

            val newMessage = ChatMessage(
                sender = myName,
                senderEmail = myEmail,
                receiverEmail = receiverEmail,
                content = text
            )
            messages.add(newMessage)
            adapter.notifyItemInserted(messages.size - 1)
            chatRecycler.smoothScrollToPosition(messages.size - 1)
            inputMessage.text.clear()

            lifecycleScope.launch {
                try {
                    ApiClient.supabase.postgrest["messages"].insert(newMessage)
                } catch (e: Exception) {
                    Log.e("SUPABASE_ERROR", e.message ?: "Error")
                }
            }
        }
    }

    private fun fetchMessages() {
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val myEmail = prefs.getString("user_email", "") ?: ""
        Toast.makeText(this, "Fetching data...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                Log.d("CHAT_VERIFY", "Fetching: Me($myEmail) with Friend($receiverEmail)")

                val history = ApiClient.supabase.postgrest["messages"]
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

                Log.d("CHAT_VERIFY", "Found ${history.size} messages in DB")

                messages.clear()
                messages.addAll(history)
                if (messages.isNotEmpty()) {
                    Log.d("DEBUG_DATA", "First message content: " + messages[0].content)
                    adapter.notifyDataSetChanged()
                    chatRecycler.scrollToPosition(messages.size - 1)
                } else {
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Log.e("FETCH_ERROR", "Failed to load history: ${e.message}")
            }
        }
    }
}