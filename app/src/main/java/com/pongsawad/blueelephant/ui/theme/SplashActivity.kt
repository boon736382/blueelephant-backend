package com.pongsawad.blueelephant.ui.theme

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
// These imports are vital if the IDE is struggling to link files
import com.pongsawad.blueelephant.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val prefs by lazy { getSharedPreferences("APP_PREFS", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure R is imported from your project package if this line is red
        // import com.pongsawad.blueelephant.R
        // setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)
            val hasProfile = prefs.getBoolean("HAS_PROFILE", false)

            val nextActivity = when {
                !isLoggedIn -> LoginActivity::class.java
                !hasProfile -> OnboardingActivity::class.java
                else -> MainActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()
        }, 1000)
    }
}