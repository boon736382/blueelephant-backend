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
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerBtn.isEnabled = false
            registerBtn.text = "Creating Account..."

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                    // Send to Onboarding to fill out profile
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("AUTH_ERROR", "Registration failed", e)
                    Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    registerBtn.isEnabled = true
                    registerBtn.text = "Register"
                }
        }
    }
}