package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pongsawad.blueelephant.network.ApiClient
import com.pongsawad.blueelephant.network.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameBox: EditText
    private lateinit var emailBox: EditText
    private lateinit var passwordBox: EditText
    private lateinit var registerBtn: Button
    private lateinit var goLoginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        nameBox = findViewById(R.id.nameBox)
        emailBox = findViewById(R.id.emailBox)
        passwordBox = findViewById(R.id.passwordBox)
        registerBtn = findViewById(R.id.registerBtn)
        goLoginBtn = findViewById(R.id.goLoginBtn)

        val apiService = ApiClient.apiService

        registerBtn.setOnClickListener {
            val name = nameBox.text.toString().trim()
            val email = emailBox.text.toString().trim()
            val password = passwordBox.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Use coroutine for suspend Retrofit call
            lifecycleScope.launch {
                try {
                    val response = apiService.register(RegisterRequest(name, email, password))
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null && body.success) {
                            Toast.makeText(this@RegisterActivity, body.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                body?.message ?: "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: response.message()
                        Toast.makeText(this@RegisterActivity, "Server error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: HttpException) {
                    Toast.makeText(this@RegisterActivity, "HTTP error: ${e.message}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        goLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
