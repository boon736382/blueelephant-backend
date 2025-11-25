package com.pongsawad.blueelephant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.LoginRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var emailBox: EditText
    private lateinit var passwordBox: EditText
    private lateinit var loginBtn: Button
    private lateinit var goRegisterBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailBox = findViewById(R.id.emailBox)
        passwordBox = findViewById(R.id.passwordBox)
        loginBtn = findViewById(R.id.loginBtn)
        goRegisterBtn = findViewById(R.id.goRegisterBtn)

        val apiService = ApiClient.apiService

        loginBtn.setOnClickListener {
            val email = emailBox.text.toString().trim()
            val password = passwordBox.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Coroutine for network call
            lifecycleScope.launch {
                try {
                    val response = apiService.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success && body.token != null) {
                            // Save JWT token locally
                            val prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
                            prefs.edit().putString("JWT_TOKEN", body.token).apply()

                            Toast.makeText(this@LoginActivity, body.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                body?.message ?: "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: response.message()
                        Toast.makeText(this@LoginActivity, "Server error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: HttpException) {
                    Toast.makeText(this@LoginActivity, "HTTP error: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        goRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
