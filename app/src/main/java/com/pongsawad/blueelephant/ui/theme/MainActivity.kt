package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.LoginActivity
import com.pongsawad.blueelephant.R

class MainActivity : AppCompatActivity() {

    private lateinit var friendBtn: Button
    private lateinit var swipeBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var exitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)

        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        // Initialize Buttons
        friendBtn = findViewById(R.id.friendBtn)
        swipeBtn = findViewById(R.id.swipeBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        exitBtn = findViewById(R.id.exitBtn)

        // Navigation: Go to FriendActivity
        friendBtn.setOnClickListener {
            val intent = Intent(this, FriendActivity::class.java)
            startActivity(intent)
        }

        swipeBtn.setOnClickListener {
            Toast.makeText(this, "Swipe Feature Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            // Logout and clear session
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }
}