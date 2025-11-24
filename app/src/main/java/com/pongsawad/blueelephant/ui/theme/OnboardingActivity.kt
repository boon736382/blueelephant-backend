package com.pongsawad.blueelephant

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val photoBtn = findViewById<Button>(R.id.photoBtn)
        val photoPreview = findViewById<ImageView>(R.id.photoPreview)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        photoBtn.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
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

            // Save info (could use SharedPreferences or send to backend)
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("gender", gender)
            intent.putExtra("age", age)
            intent.putExtra("photoUri", imageUri.toString())
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.photoPreview).setImageURI(imageUri)
        }
    }
}
