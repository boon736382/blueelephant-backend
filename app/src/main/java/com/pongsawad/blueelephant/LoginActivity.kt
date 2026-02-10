package com.pongsawad.blueelephant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.LoginRequest // Create this if you haven't
import com.pongsawad.blueelephant.ui.theme.MainActivity
import com.pongsawad.blueelephant.ui.theme.OnboardingActivity
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity() {

    private lateinit var emailBox: EditText
    private lateinit var passwordBox: EditText
    private lateinit var loginBtn: Button
    private lateinit var toRegister: TextView // Added this

    private val prefs by lazy { getSharedPreferences("APP_PREFS", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailBox = findViewById(R.id.emailBox)
        passwordBox = findViewById(R.id.passwordBox)
        loginBtn = findViewById(R.id.loginBtn)
        toRegister = findViewById(R.id.toRegister) // Link the ID

        // Navigation to Register Screen
        toRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            val email = emailBox.text.toString().trim()
            val password = passwordBox.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginBtn.isEnabled = false
            loginBtn.text = "Logging in..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = LoginRequest(email, password)
                    val response = ApiClient.apiService.login(request)

                    withContext(Dispatchers.Main) {
                        // Inside LoginActivity.kt

                        // 1. Change the redirection logic in the success block
                        if (response.isSuccessful) {
                            val body = response.body()
                            val user = body?.user // Extract the user object from the response

                            prefs.edit().apply {
                                putBoolean("IS_LOGGED_IN", true)

                                // --- THE FIX: Save all user details ---
                                putString("user_name", user?.name)
                                putString("user_age", user?.age?.toString()) // Convert Int to String
                                putString("user_gender", user?.gender)
                                putString("user_photo_path", user?.profile_image)
                                putString("user_email", email)

                                body?.token?.let { putString("USER_TOKEN", it) }
                                apply()
                            }

                            // Check if onboarding is complete
                            // Note: If the user already has a name and age from the server,
                            // you might want to force this to 'true' automatically.
                            val isOnboardingDone = prefs.getBoolean("is_onboarding_complete", false) || !user?.name.isNullOrEmpty()

                            if (!user?.name.isNullOrEmpty()) {
                                prefs.edit().putBoolean("is_onboarding_complete", true).apply()
                            }

                            val nextActivity = if (prefs.getBoolean("is_onboarding_complete", false)) {
                                MainActivity::class.java
                            } else {
                                OnboardingActivity::class.java
                            }

                            val intent = Intent(this@LoginActivity, nextActivity)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            // Handle wrong password/email
                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"
                            Toast.makeText(this@LoginActivity, "Invalid Login Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        loginBtn.isEnabled = true
                        loginBtn.text = "Login"
                        Toast.makeText(this@LoginActivity, "Server Connection Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}