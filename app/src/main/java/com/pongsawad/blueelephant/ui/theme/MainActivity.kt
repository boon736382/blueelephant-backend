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

        // --- THE GATEKEEPER CHECK ---
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        val hasProfile = prefs.getBoolean("HAS_PROFILE", false)

        if (!isLoggedIn) {
            // User hasn't logged in yet
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else if (!hasProfile) {
            // Logged in but never finished onboarding (photo/age/gender)
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        // ----------------------------

        setContentView(R.layout.activity_main_chat)

        // Initialize Buttons
        friendBtn = findViewById(R.id.friendBtn)
        swipeBtn = findViewById(R.id.swipeBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        exitBtn = findViewById(R.id.exitBtn)

        // Navigation: Friends List
        friendBtn.setOnClickListener {
            startActivity(Intent(this, FriendActivity::class.java))
        }

        // Navigation: Swipe Profiles
        swipeBtn.setOnClickListener {
            Toast.makeText(this, "Opening Swipe...", Toast.LENGTH_SHORT).show()
        }

        // Logout Logic
        logoutBtn.setOnClickListener {
            // Clears everything: Token, Profile Flag, Image Path, etc.
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
        }

        // Exit App
        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }
}