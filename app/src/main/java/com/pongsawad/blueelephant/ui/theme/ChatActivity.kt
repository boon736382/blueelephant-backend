package com.pongsawad.blueelephant

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var messagesRecycler: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendBtn: Button

    private val messagesList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messagesRecycler = findViewById(R.id.messagesRecycler)
        messageInput = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)

        adapter = ChatAdapter(messagesList)
        messagesRecycler.layoutManager = LinearLayoutManager(this)
        messagesRecycler.adapter = adapter

        sendBtn.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val message = ChatMessage(
                    text = text,
                    timestamp = System.currentTimeMillis()
                )
                messagesList.add(message)
                adapter.notifyItemInserted(messagesList.size - 1)
                messagesRecycler.scrollToPosition(messagesList.size - 1)
                messageInput.text.clear()

                // Schedule auto-delete after 1 minute
                handler.postDelayed({
                    messagesList.remove(message)
                    adapter.notifyDataSetChanged()
                }, 60_000) // 60,000 ms = 1 minute
            }
        }
    }
}
