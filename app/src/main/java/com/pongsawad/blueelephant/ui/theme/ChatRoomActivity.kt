package com.pongsawad.blueelephant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.com.pongsawad.blueelephant.ui.theme.Message

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendBtn: Button

    private val messageList = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecycler = findViewById(R.id.messagesRecycler)
        inputMessage = findViewById(R.id.messageInput)
        sendBtn = findViewById(R.id.sendBtn)

        adapter = MessageAdapter(messageList)
        chatRecycler.layoutManager = LinearLayoutManager(this)
        chatRecycler.adapter = adapter

        sendBtn.setOnClickListener {
            val text = inputMessage.text.toString()

            if (text.isNotEmpty()) {

                // Add My Message
                messageList.add(Message(text, isMe = true))
                adapter.notifyItemInserted(messageList.size - 1)
                chatRecycler.scrollToPosition(messageList.size - 1)

                inputMessage.text.clear()

                // Optional fake reply
                messageList.add(Message("Okay!", isMe = false))
                adapter.notifyItemInserted(messageList.size - 1)
                chatRecycler.scrollToPosition(messageList.size - 1)
            }
        }
    }
}
