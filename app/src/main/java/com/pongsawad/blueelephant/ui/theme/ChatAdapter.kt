package com.pongsawad.blueelephant

import android.graphics.Color // Added for color constants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.R

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSender: TextView = itemView.findViewById(R.id.tv_sender)
        val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMsg = messages[position]
        holder.tvSender.text = chatMsg.sender
        holder.tvMessage.text = chatMsg.content

        // This forces the message text to be Black
        holder.tvMessage.setTextColor(Color.BLACK)

        // Optional: If you want the sender's name (like "Me") to be black too:
        holder.tvSender.setTextColor(Color.BLACK)
    }

    override fun getItemCount() = messages.size
}