package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pongsawad.blueelephant.LoginActivity
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var friendBtn: Button
    private lateinit var swipeBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var exitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val isOnboardingComplete = prefs.getBoolean("is_onboarding_complete", false)
        val userEmail = prefs.getString("user_email", null)

        // 1. Session Check
        if (userEmail == null) {
            navigateToLogin()
            return
        } else if (!isOnboardingComplete) {
            navigateToOnboarding()
            return
        }

        setContentView(R.layout.activity_main_chat)

        // Initialize Buttons
        friendBtn = findViewById(R.id.friendBtn)
        swipeBtn = findViewById(R.id.swipeBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        exitBtn = findViewById(R.id.exitBtn)

        friendBtn.setOnClickListener {
            startActivity(Intent(this, FriendActivity::class.java))
        }

        swipeBtn.setOnClickListener {
            Toast.makeText(this, "Swipe Feature Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        // 2. Modified Logout with API Status Update
        logoutBtn.setOnClickListener {
            performLogout(prefs)
        }

        exitBtn.setOnClickListener {
            finishAffinity()
        }
    }

    private fun performLogout(prefs: android.content.SharedPreferences) {
        val userId = prefs.getInt("user_id", -1)

        // Optional: Call backend to set status to 'Offline'
        lifecycleScope.launch {
            try {
                if (userId != -1) {
                    // You would need a logout endpoint or use a status update map
                    // ApiClient.apiService.logout(userId)
                }
            } catch (e: Exception) {
                // Ignore network errors on logout
            } finally {
                // Clear local data and move to login
                prefs.edit().clear().apply()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }
}