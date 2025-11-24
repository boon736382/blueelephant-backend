package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.LoginRequest
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

        loginBtn.setOnClickListener { performLogin() }

        goRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun performLogin() {
        val email = emailBox.text.toString().trim()
        val password = passwordBox.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        val call: Call<LoginResponse> = ApiClient.apiService.login(LoginRequest(email, password))
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@LoginActivity, "Welcome!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
