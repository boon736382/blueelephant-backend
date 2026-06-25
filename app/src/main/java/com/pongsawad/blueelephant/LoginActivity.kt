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
import com.pongsawad.blueelephant.network.UserData
import com.pongsawad.blueelephant.ui.theme.MainActivity
import com.pongsawad.blueelephant.ui.theme.OnboardingActivity
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
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
            val email = emailBox.text.toString().trim().lowercase() // Add .lowercase()
            val password = passwordBox.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginBtn.isEnabled = false
            loginBtn.text = "Logging in..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // 1. Authenticate with Supabase Auth
                    ApiClient.supabase.auth.signInWith(Email) {
                        this.email = email
                        this.password = password
                    }

                    // 2. Fetch User Profile Details from PostgreSQL 'users' table
                    val user = ApiClient.supabase.postgrest["users"].select {
                        filter {
                            eq("email", email)
                        }
                    }.decodeSingleOrNull<UserData>()

                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            // Use ONE editor for the entire transaction
                            prefs.edit().apply {
                                // 1. Session State
                                putBoolean("IS_LOGGED_IN", true)
                                putString("USER_TOKEN", ApiClient.supabase.auth.currentAccessTokenOrNull())

                                // 2. User Credentials (Keep for Onboarding/Re-auth)
                                putString("user_email", email)
                                putString("user_password", password)

                                // 3. Profile Details from PostgreSQL
                                putString("user_name", user.name)
                                putString("user_age", user.age?.toString())
                                putString("user_gender", user.gender)
                                putString("user_photo_path", user.profile_image)

                                // 4. Smart Onboarding Check
                                val isReallyComplete = !user.name.isNullOrEmpty() && user.name != "New User"
                                putBoolean("is_onboarding_complete", isReallyComplete)

                                apply() // Save everything at once
                            }

                            // Determine the next screen
                            val isDone = prefs.getBoolean("is_onboarding_complete", false)
                            val nextActivity = if (isDone) MainActivity::class.java else OnboardingActivity::class.java

                            val intent = Intent(this@LoginActivity, nextActivity)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()

                        } else {
                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"
                            Toast.makeText(this@LoginActivity, "User data not found in database", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        loginBtn.isEnabled = true
                        loginBtn.text = "Login"
                        val errorMessage = e.message ?: "Login Failed"
                        if (errorMessage.contains("Invalid login credentials", ignoreCase = true)) {
                            Toast.makeText(this@LoginActivity, "Wrong email or password", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}