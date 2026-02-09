package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class OnboardingActivity : AppCompatActivity() {

    private var imageFile: File? = null
    // Consistent preference key
    private val PREFS_NAME = "APP_PREFS"
    private val ONBOARDING_KEY = "is_onboarding_complete"
    private val prefs by lazy { getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val localFile = File(filesDir, "profile_upload.jpg")
                contentResolver.openInputStream(uri)?.use { input ->
                    localFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                imageFile = localFile
                findViewById<ImageView>(R.id.photoPreview).setImageURI(Uri.fromFile(localFile))
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to process photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CHECK: If already complete, skip this activity entirely
        if (prefs.getBoolean(ONBOARDING_KEY, false)) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_onboarding)

        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val photoBtn = findViewById<Button>(R.id.photoBtn)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        photoBtn.setOnClickListener { pickImageLauncher.launch("image/*") }

        submitBtn.setOnClickListener {
            val selectedGenderId = genderGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else ""
            val age = ageInput.text.toString()
            val name = nameInput.text.toString()

            if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || imageFile == null) {
                Toast.makeText(this, "Complete all fields first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitBtn.isEnabled = false
            submitBtn.text = "Sending..."

            saveProfileToApi(name, age, gender, imageFile!!)
        }
    }

    private fun saveProfileToApi(name: String, age: String, gender: String, file: File) {
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        // Ensure these match whatever you used during the actual Registration/Login step
        val email = prefs.getString("user_email", "test@example.com") ?: "test@example.com"
        val password = prefs.getString("user_password", "password123") ?: "password123"

        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.uploadProfile(
                    emailPart, passwordPart, namePart, agePart, genderPart, imagePart
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@OnboardingActivity, "Success!", Toast.LENGTH_SHORT).show()
                    markCompleteAndNavigate()
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains("already registered")) {
                        Toast.makeText(this@OnboardingActivity, "Account exists. Logging in...", Toast.LENGTH_SHORT).show()
                        markCompleteAndNavigate()
                    } else {
                        findViewById<Button>(R.id.submitBtn).isEnabled = true
                        findViewById<Button>(R.id.submitBtn).text = "Submit"
                        Toast.makeText(this@OnboardingActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed: ${e.message}")
                findViewById<Button>(R.id.submitBtn).isEnabled = true
                findViewById<Button>(R.id.submitBtn).text = "Retry"
                Toast.makeText(this@OnboardingActivity, "Server unreachable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markCompleteAndNavigate() {
        prefs.edit().putBoolean(ONBOARDING_KEY, true).apply()
        navigateToMain()
    }

    private fun navigateToMain() {
        // Change "MainActivity" to "MainActivityChat" if that is your chat class name
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}