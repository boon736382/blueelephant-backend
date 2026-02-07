package com.pongsawad.blueelephant.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.ui.theme.FriendActivity

class MainChatActivity : AppCompatActivity() {

    private lateinit var friendBtn: Button
    private lateinit var exitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)

        // Match EXACT IDs from activity_main_chat.xml
        friendBtn = findViewById(R.id.friendBtn)
        exitBtn = findViewById(R.id.exitBtn)

        // Open Friend List (FriendActivity)
        friendBtn.setOnClickListener {
            val intent = Intent(this, FriendActivity::class.java)
            startActivity(intent)
        }

        // Open Swipe Activity (FriendSwipeActivity)
        // Close App
        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }
}
