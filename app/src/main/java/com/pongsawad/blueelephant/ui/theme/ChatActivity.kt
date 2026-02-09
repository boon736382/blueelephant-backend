package com.pongsawad.blueelephant.ui.theme

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
    private val messages = mutableListOf<ChatMessage>() // This must match the name in the adapter
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
        chatRecycler.layoutManager = LinearLayoutManager(this)

        // 4. Button Logic
        sendBtn.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                // Change this line to use 'content'
                messages.add(ChatMessage("Me", text))

                adapter.notifyItemInserted(messages.size - 1)
                chatRecycler.scrollToPosition(messages.size - 1)
                inputMessage.text.clear()
            }
        }
    }
}