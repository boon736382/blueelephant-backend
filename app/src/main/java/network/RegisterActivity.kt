package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.pongsawad.blueelephant.ui.theme.OnboardingActivity
import androidx.lifecycle.lifecycleScope
import com.pongsawad.blueelephant.network.ApiClient
import kotlinx.coroutines.launch
import android.content.Context
import com.pongsawad.blueelephant.network.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Force App Check to allow this physical phone
        FirebaseApp.initializeApp(this)
        val appCheck = FirebaseAppCheck.getInstance()
        appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerBtn.isEnabled = false
            registerBtn.text = "Creating Account..."

            // 1. First, create the user in Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // 2. Firebase Success! Now create the user in your PostgreSQL Database
                    lifecycleScope.launch {
                        try {
                            val request = RegisterRequest(
                                email = email,
                                password = password,
                                name = "New User",
                            )
                            val response = ApiClient.apiService.register(request)

                            if (response.isSuccessful) {
                                // 3. SUCCESS: Save email for the Onboarding screen
                                val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                                prefs.edit().apply {
                                    putString("user_email", email)
                                    putBoolean("IS_LOGGED_IN", true)
                                    apply()
                                }

                                Toast.makeText(this@RegisterActivity, "Account Created in DB!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@RegisterActivity, OnboardingActivity::class.java))
                                finish()
                            } else {
                                Log.e("DB_ERROR", response.errorBody()?.string() ?: "Unknown error")
                                Toast.makeText(this@RegisterActivity, "Firebase OK, but DB Failed", Toast.LENGTH_SHORT).show()
                                registerBtn.isEnabled = true
                            }
                        } catch (e: Exception) {
                            Log.e("API_ERROR", e.message ?: "Error")
                            registerBtn.isEnabled = true
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Firebase Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    registerBtn.isEnabled = true
                    registerBtn.text = "Register"
                }
        }
    }
}