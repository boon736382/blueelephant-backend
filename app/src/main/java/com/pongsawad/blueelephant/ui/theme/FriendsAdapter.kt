package com.pongsawad.blueelephant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.R


class FriendAdapter(
    private val friends: List<Friend>,
    private val onClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    // Inside FriendAdapter class
    fun updateData(newFriends: List<Friend>) {
        // This ensures the adapter's internal list is replaced with the fresh data from Render
        (friends as MutableList).clear()
        friends.addAll(newFriends)
        notifyDataSetChanged()
    }
    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ensure these IDs (friendName and friendAvatar) exist inside item_friend_card.xml
        val tvName: TextView = itemView.findViewById(R.id.friendName)
        val ivAvatar: ImageView = itemView.findViewById(R.id.friendAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        // Changed R.layout.item_friend to R.layout.item_friend_card to match your image
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_card, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.tvName.text = friend.name
        holder.ivAvatar.setImageResource(R.mipmap.ic_launcher) // placeholder
        holder.itemView.setOnClickListener { onClick(friend) }
    }

    override fun getItemCount() = friends.size
}