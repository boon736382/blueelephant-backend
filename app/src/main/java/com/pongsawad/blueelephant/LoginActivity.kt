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
                            val user = body?.user

                            // Use ONE editor for the entire transaction
                            prefs.edit().apply {
                                // 1. Session State
                                putBoolean("IS_LOGGED_IN", true)
                                putString("USER_TOKEN", body?.token)

                                // 2. User Credentials (Keep for Onboarding/Re-auth)
                                putString("user_email", email)
                                putString("user_password", password)

                                // 3. Profile Details from PostgreSQL
                                putString("user_name", user?.name)
                                putString("user_age", user?.age?.toString())
                                putString("user_gender", user?.gender)
                                putString("user_photo_path", user?.profile_image)

                                // 4. Smart Onboarding Check
                                // If the name is null, empty, or still the default "New User", force onboarding
                                val isReallyComplete = !user?.name.isNullOrEmpty() && user?.name != "New User"
                                putBoolean("is_onboarding_complete", isReallyComplete)

                                apply() // Save everything at once
                            }

                            // Determine the next screen based on the value we just saved
                            val isDone = prefs.getBoolean("is_onboarding_complete", false)
                            val nextActivity = if (isDone) MainActivity::class.java else OnboardingActivity::class.java

                            val intent = Intent(this@LoginActivity, nextActivity)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        } else {
                            // Handling specific error feedback
                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"
                            val errorBody = response.errorBody()?.string() ?: ""

                            if (errorBody.contains("Invalid")) {
                                Toast.makeText(this@LoginActivity, "Wrong email or password", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Failed: $errorBody", Toast.LENGTH_SHORT).show()
                            }
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