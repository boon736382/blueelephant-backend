package com.pongsawad.blueelephant

import android.graphics.Color // Added for color constants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.R

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: String // Pass your email or ID here
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Define two types of views
    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tv_message)
        // Note: For a clean bubble look, we usually hide tvSender
        // and just use different colors for the bubbles.
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderEmail == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_SENT) {
            R.layout.item_message_sent // Create this with a black background
        } else {
            R.layout.item_message_received // Create this with a gray background
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val chatMsg = messages[position]
        holder.tvMessage.text = chatMsg.content

        // Sent messages: Black bubble, White text
        // Received messages: Gray bubble, Black text
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            holder.tvMessage.setTextColor(Color.WHITE)
        } else {
            holder.tvMessage.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = messages.size
}