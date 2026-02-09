package com.pongsawad.blueelephant

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView

// Added (context, attrs) constructor so it can be used in XML if needed
class FriendCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val avatar: ImageView
    private val nameText: TextView
    private val statusText: TextView // Changed from emailText to match Friend data class

    init {
        orientation = HORIZONTAL // Standard for a friend card
        LayoutInflater.from(context).inflate(R.layout.item_friend_card, this, true)

        avatar = findViewById(R.id.friendAvatar)
        nameText = findViewById(R.id.friendName)
        statusText = findViewById(R.id.view_status_dot) // Ensure this ID exists in XML
    }

    fun bind(friend: Friend) {
        nameText.text = friend.name

        // Use a placeholder image for now
        avatar.setImageResource(R.mipmap.ic_launcher_round)
    }
}