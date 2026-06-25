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
import com.pongsawad.blueelephant.network.UserData
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import android.content.Context

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

            lifecycleScope.launch {
                try {
                    // 1. Create the user in Supabase Auth
                    ApiClient.supabase.auth.signUpWith(Email) {
                        this.email = email
                        this.password = password
                    }

                    // 2. Create the user record in your PostgreSQL 'users' table
                    val newUser = UserData(
                        email = email,
                        name = "New User"
                    )
                    
                    ApiClient.supabase.postgrest["users"].insert(newUser)

                    // 3. SUCCESS: Save session details
                    val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("user_email", email)
                        putString("user_password", password) // Useful for re-auth
                        putBoolean("IS_LOGGED_IN", true)
                        putString("USER_TOKEN", ApiClient.supabase.auth.currentAccessTokenOrNull())
                        apply()
                    }

                    Toast.makeText(this@RegisterActivity, "Account Created!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, OnboardingActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Log.e("SUPABASE_ERROR", e.message ?: "Error")
                    Toast.makeText(this@RegisterActivity, "Registration Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    registerBtn.isEnabled = true
                    registerBtn.text = "Register"
                }
            }
        }
    }
}