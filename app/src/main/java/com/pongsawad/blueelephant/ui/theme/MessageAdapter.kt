package com.pongsawad.blueelephant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.com.pongsawad.blueelephant.ui.theme.Message

class MessageAdapter(private val messageList: List<Message>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ME = 1
    private val TYPE_FRIEND = 2

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isMe) TYPE_ME else TYPE_FRIEND
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ME) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_me, parent, false)
            MeViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_friend, parent, false)
            FriendViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messageList[position]

        if (holder is MeViewHolder)
            holder.bind(msg)

        if (holder is FriendViewHolder)
            holder.bind(msg)
    }

    // -------------------------
    // VIEW HOLDER: ME (RIGHT)
    // -------------------------
    class MeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: Message) {
            messageText.text = message.text
        }
    }

    // -------------------------
    // VIEW HOLDER: FRIEND (LEFT)
    // -------------------------
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: Message) {
            messageText.text = message.text
        }
    }
}
