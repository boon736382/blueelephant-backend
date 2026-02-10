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
        this.friends = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_card, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.tvName.text = friend.name ?: "Unknown User"
        holder.tvStatusText.text = friend.status ?: "Offline"

        // --- STATUS DOT ---
        val statusColor = if (friend.status.equals("Online", ignoreCase = true)) "#4CAF50" else "#9E9E9E"
        holder.viewStatusDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(statusColor))

        // --- FIXED IMAGE LOADING ---
        val baseUrl = "https://blueelephant-backend.onrender.com/"
        val finalUrl = if (friend.imageUrl?.startsWith("http") == true) {
            friend.imageUrl
        } else {
            baseUrl + friend.imageUrl
        }
        android.util.Log.d("DEBUG_IMAGE", "Loading URL: $finalUrl")

        Glide.with(holder.itemView.context)
            .load(finalUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground) // If this shows up, URL is 404
            .circleCrop()
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener { onClick(friend) }
    }

    override fun getItemCount() = friends.size

    // FIXED: Removed the double class declaration and corrected the casts
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.friendName)
        val ivAvatar: ImageView = itemView.findViewById(R.id.friendAvatar)

        // This is for the actual Text (e.g., "Online")
        // Make sure you have a TextView in item_friend_card with this ID!
        val tvStatusText: TextView = itemView.findViewById(R.id.friendStatusText)

        // This is for the Green/Gray circle dot
        // This must be 'View' to match your XML View tag
        val viewStatusDot: View = itemView.findViewById(R.id.view_status_dot)
    }
}