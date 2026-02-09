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
    private val prefs by lazy { getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE) }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                // Creates a local copy of the image to send to your backend
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
        setContentView(R.layout.activity_onboarding)

        // NOTE: Firebase initialization removed here because we are going direct to API

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
            submitBtn.text = "Sending to Server..."

            // DIRECT CALL: Skip Firebase, go straight to your Render Backend
            saveProfileToApi(name, age, gender, imageFile!!)
        }
    }

    private fun saveProfileToApi(name: String, age: String, gender: String, file: File) {
        // 1. Prepare text parts (Matching your authController.js keys)
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        // Pulling saved registration info (or using defaults) to pass backend validator
        val email = prefs.getString("user_email", "test@example.com") ?: "test@example.com"
        val password = prefs.getString("user_password", "password123") ?: "password123"

        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())

        // 2. Prepare the Image File part (Matching Multer's upload.single('profile_image'))
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.uploadProfile(
                    emailPart, passwordPart, namePart, agePart, genderPart, imagePart
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@OnboardingActivity, "Profile Created Successfully!", Toast.LENGTH_SHORT).show()
                    // navigate to FriendActivity or MainActivity
                    // startActivity(Intent(this@OnboardingActivity, FriendActivity::class.java))
                    finish()
                } else {
                    val errorLog = response.errorBody()?.string()
                    Log.e("API_ERROR", "Status: ${response.code()} - $errorLog")
                    Toast.makeText(this@OnboardingActivity, "Server Error: $errorLog", Toast.LENGTH_LONG).show()
                    findViewById<Button>(R.id.submitBtn).isEnabled = true
                    findViewById<Button>(R.id.submitBtn).text = "Submit"
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Connection failed: ${e.message}")
                Toast.makeText(this@OnboardingActivity, "Check Internet / Server offline", Toast.LENGTH_SHORT).show()
                findViewById<Button>(R.id.submitBtn).isEnabled = true
            }
        }
    }
}