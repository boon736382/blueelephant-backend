package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainChatActivity : AppCompatActivity() {

    private lateinit var friendBtn: Button
    private lateinit var swipeBtn: Button
    private lateinit var exitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)

        // Match EXACT IDs from activity_main_chat.xml
        friendBtn = findViewById(R.id.friendBtn)
        swipeBtn = findViewById(R.id.swipeBtn)
        exitBtn = findViewById(R.id.exitBtn)

        // Open Friend List (FriendActivity)
        friendBtn.setOnClickListener {
            val intent = Intent(this, FriendActivity::class.java)
            startActivity(intent)
        }

        // Open Swipe Activity (FriendSwipeActivity)
        swipeBtn.setOnClickListener {
            val intent = Intent(this, FriendSwipeActivity::class.java)
            startActivity(intent)
        }

        // Close App
        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }
}
