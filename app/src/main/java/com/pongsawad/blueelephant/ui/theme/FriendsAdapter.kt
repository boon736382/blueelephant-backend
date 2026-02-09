package com.pongsawad.blueelephant

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class FriendAdapter(
    private var friends: List<Friend>,
    private val onClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    fun updateData(newList: List<Friend>) {
        this.friends = newList.toMutableList()
        notifyDataSetChanged() // This tells the UI to actually draw the items
    }

    // 1. MUST HAVE: This tells the adapter which layout file to use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_card, parent, false)
        return FriendViewHolder(view)
    }

    // 2. Logic to put data into the views
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.tvName.text = friend.name ?: "Unknown User"

        // --- DYNAMIC STATUS DOT ---
        val statusDot = holder.itemView.findViewById<View>(R.id.view_status_dot)

        if (friend.status.equals("Online", ignoreCase = true)) {
            // Vibrant Green for Online
            statusDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            holder.tvStatus.text = "Online"
        } else {
            // Soft Gray for Offline
            statusDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            holder.tvStatus.text = "Offline"
        }

        // --- FAIL-SAFE IMAGE LOADING ---
        Glide.with(holder.itemView.context)
            .load(friend.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_default_avatar) // Use a silhouette icon
            .circleCrop()
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener { onClick(friend) }
    }

    override fun getItemCount() = friends.size

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.friendName)
        val ivAvatar: ImageView = itemView.findViewById(R.id.friendAvatar)
        val tvStatus: TextView = itemView.findViewById(R.id.view_status_dot)
    }
}