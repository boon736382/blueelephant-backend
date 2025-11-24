package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.LoginRequest
import com.pongsawad.blueelephant.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailBox: EditText
    private lateinit var passwordBox: EditText
    private lateinit var loginBtn: Button
    private lateinit var goRegisterBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailBox = findViewById(R.id.emailBox)
        passwordBox = findViewById(R.id.passwordBox)
        loginBtn = findViewById(R.id.loginBtn)
        goRegisterBtn = findViewById(R.id.goRegisterBtn)

        loginBtn.setOnClickListener {
            val email = emailBox.text.toString().trim()
            val password = passwordBox.text.toString().trim()

            if (email.isEmpty()) {
                emailBox.error = "Email is required"
                emailBox.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordBox.error = "Password is required"
                passwordBox.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordBox.error = "Password must be at least 6 characters"
                passwordBox.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        goRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email, password)

        ApiClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.token != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Optionally, save the token in SharedPreferences
                        val prefs = getSharedPreferences("BlueElephantPrefs", MODE_PRIVATE)
                        prefs.edit().putString("JWT_TOKEN", body.token).apply()

                        // Go to main activity
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Server error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
