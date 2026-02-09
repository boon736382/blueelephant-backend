package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.LoginActivity

class SplashActivity : AppCompatActivity() {

    // Centralize your keys to prevent typos
    private val PREFS_NAME = "APP_PREFS"
    private val KEY_LOGGED_IN = "IS_LOGGED_IN"
    private val KEY_ONBOARDING_COMPLETE = "is_onboarding_complete" // Match the key in OnboardingActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: If you have a splash layout, uncomment this:
        // setContentView(R.layout.activity_splash)

        // Using a shorter delay (500ms - 1s) is usually better for user experience
        Handler(Looper.getMainLooper()).postDelayed({
            checkNavigation()
        }, 800)
    }

    private fun checkNavigation() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false)
        val isOnboardingDone = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

        val nextActivity = when {
            !isLoggedIn -> LoginActivity::class.java
            !isOnboardingDone -> OnboardingActivity::class.java
            else -> MainActivity::class.java
        }

        // The "Single Top" flag helps prevent the "Rapid Launch" kill signal from Android
        val intent = Intent(this, nextActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
        finish()
    }
}