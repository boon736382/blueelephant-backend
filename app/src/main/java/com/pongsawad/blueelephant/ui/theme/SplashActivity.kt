package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.LoginActivity
import com.pongsawad.blueelephant.R

class SplashActivity : AppCompatActivity() {

    private lateinit var friendBtn: Button
    private lateinit var swipeBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var exitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

        // --- UPDATED GATEKEEPER CHECK ---
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        // Match the key name used in Splash and Onboarding!
        val isOnboardingDone = prefs.getBoolean("is_onboarding_complete", false)

        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else if (!isOnboardingDone) {
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

        // Navigation
        friendBtn.setOnClickListener {
            // Make sure FriendActivity is created or this will crash!
            // startActivity(Intent(this, FriendActivity::class.java))
            Toast.makeText(this, "Opening Friends...", Toast.LENGTH_SHORT).show()
        }

        swipeBtn.setOnClickListener {
            Toast.makeText(this, "Opening Swipe...", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            // BE CAREFUL: prefs.edit().clear() deletes EVERYTHING including saved Email/Password.
            // If you want to keep the email for the login box, use remove() for specific keys.
            prefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
        }

        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }
}