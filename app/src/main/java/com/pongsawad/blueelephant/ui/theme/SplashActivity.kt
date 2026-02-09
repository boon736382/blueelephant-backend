package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get Preferences
        val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
        val isOnboardingDone = prefs.getBoolean("is_onboarding_complete", false)

        // 2. Decide where to go
        val nextActivity = when {
            !isLoggedIn -> LoginActivity::class.java
            !isOnboardingDone -> OnboardingActivity::class.java
            else -> MainActivity::class.java
        }

        // 3. Launch and Kill Splash so it's not in the back-stack
        val intent = Intent(this, nextActivity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}