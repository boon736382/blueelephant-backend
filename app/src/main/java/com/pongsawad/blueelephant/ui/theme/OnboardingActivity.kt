package com.pongsawad.blueelephant.ui.theme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.pongsawad.blueelephant.ui.theme.MainChatActivity
import com.pongsawad.blueelephant.R

class OnboardingActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private val prefs by lazy { getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE) }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            findViewById<ImageView>(R.id.photoPreview).setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val photoBtn = findViewById<Button>(R.id.photoBtn)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        photoBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        submitBtn.setOnClickListener {
            val selectedGenderId = genderGroup.checkedRadioButtonId
            if (selectedGenderId == -1) {
                Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val gender = findViewById<RadioButton>(selectedGenderId).text.toString()
            val age = ageInput.text.toString()
            if (age.isEmpty()) {
                Toast.makeText(this, "Enter age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (imageUri == null) {
                Toast.makeText(this, "Select a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save profile state
            prefs.edit().putBoolean("HAS_PROFILE", true).apply()

            // Optionally save profile info: gender, age, photoUri
            prefs.edit().putString("PROFILE_GENDER", gender)
            prefs.edit().putString("PROFILE_AGE", age)
            prefs.edit().putString("PROFILE_PHOTO_URI", imageUri.toString()).apply()

            // Go to main chat
            startActivity(Intent(this, MainChatActivity::class.java))
            finish()
        }
    }
}
