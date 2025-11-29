package com.pongsawad.blueelephant

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var chatTitle: TextView
    private lateinit var messagesRecycler: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendBtn: Button

    private val messagesList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatTitle = findViewById(R.id.chatTitle)
        messagesRecycler = findViewById(R.id.messagesRecycler)
        messageInput = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)

        val friendName = intent.getStringExtra("FRIEND_NAME") ?: "Friend"
        chatTitle.text = "Chat with $friendName"

        adapter = ChatAdapter(messagesList)
        messagesRecycler.layoutManager = LinearLayoutManager(this)
        messagesRecycler.adapter = adapter

        sendBtn.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            val chatMessage = ChatMessage(text, System.currentTimeMillis())
            adapter.addMessage(chatMessage)
            messagesRecycler.scrollToPosition(messagesList.size - 1)
            messageInput.text.clear()

            // Auto delete after 1 minute
            handler.postDelayed({
                val index = messagesList.indexOf(chatMessage)
                if (index != -1) adapter.removeMessage(index)
            }, 60000)
        }
    }
}
