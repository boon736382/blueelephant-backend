package com.pongsawad.blueelephant

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

    // Use ChatMessage list
    private val messageList = mutableListOf<ChatMessage>()

    // Ensure you use ChatAdapter (since MessageAdapter had errors in your screenshot)
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1. Initialize Views
        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)
        tvTitle = findViewById(R.id.tv_chat_partner_name)

        // 2. Get data from FriendActivity
        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Chat"
        tvTitle.text = friendName

        // 3. Setup RecyclerView with ChatAdapter
        adapter = ChatAdapter(messageList)
        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.adapter = adapter

        // 4. Send Message Logic
        sendBtn.setOnClickListener {
            val text = inputMessage.text.toString().trim()

            if (text.isNotEmpty()) {
                // FIXED: Match the ChatMessage data class (sender, content)
                val myMsg = ChatMessage(sender = "Me", content = text)

                messageList.add(myMsg)
                adapter.notifyItemInserted(messageList.size - 1)
                chatRecycler.scrollToPosition(messageList.size - 1)

                inputMessage.text.clear()
            }
        }
    }
}