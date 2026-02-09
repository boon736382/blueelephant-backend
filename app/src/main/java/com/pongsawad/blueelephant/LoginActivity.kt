package com.pongsawad.blueelephant

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
                        if (response.isSuccessful) {
                            val body = response.body()

                            // Saving User Preferences
                            prefs.edit().apply {
                                putBoolean("IS_LOGGED_IN", true)
                                body?.token?.let { putString("USER_TOKEN", it) }
                                apply()
                            }

                            val hasProfile = prefs.getBoolean("HAS_PROFILE", false)
                            val nextActivity = if (hasProfile) MainActivity::class.java else OnboardingActivity::class.java

                            startActivity(Intent(this@LoginActivity, nextActivity))
                            finish()
                        } else {
                            loginBtn.isEnabled = true
                            loginBtn.text = "Login"
                            // If backend is working correctly, this will show the message from your JS controller
                            Toast.makeText(this@LoginActivity, "Login Failed: Check Email/Password", Toast.LENGTH_SHORT).show()
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