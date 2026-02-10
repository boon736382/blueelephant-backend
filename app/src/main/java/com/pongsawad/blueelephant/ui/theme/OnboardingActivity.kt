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
import okhttp3.RequestBody
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

        // Use the email saved during Login/Register
        val email = prefs.getString("user_email", "")?.trim()?.lowercase() ?: ""
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        // Ensure "profile_image" matches the key in your Node.js upload.single('profile_image')
        val imagePart = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)

        if (email.isEmpty()) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, com.pongsawad.blueelephant.LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                // --- FIX: Change uploadProfile to updateProfile ---
                val response = ApiClient.apiService.updateProfile(
                    emailPart, namePart, agePart, genderPart, imagePart
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val serverUser = body?.user

                    prefs.edit().apply {
                        putBoolean(ONBOARDING_KEY, true)
                        putString("user_name", name)
                        putString("user_age", age)
                        putString("user_gender", gender)

                        // Save the real URL/Path returned by Render
                        putString("user_photo_path", serverUser?.profile_image)
                        apply()
                    }

                    Log.d("ONBOARDING", "Profile updated. Image: ${serverUser?.profile_image}")
                    Toast.makeText(this@OnboardingActivity, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("ONBOARDING_FAIL", errorBody)

                    resetButton()
                    Toast.makeText(this@OnboardingActivity, "Update Failed: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed: ${e.message}")
                resetButton()
                Toast.makeText(this@OnboardingActivity, "Server unreachable", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetButton() {
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        submitBtn.isEnabled = true
        submitBtn.text = "Submit"
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