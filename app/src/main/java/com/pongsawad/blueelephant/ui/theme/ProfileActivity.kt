package com.pongsawad.blueelephant

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val gender = intent.getStringExtra("gender")
        val age = intent.getStringExtra("age")
        val photoUri = intent.getStringExtra("photoUri")

        val genderText = findViewById<TextView>(R.id.genderText)
        val ageText = findViewById<TextView>(R.id.ageText)
        val profilePhoto = findViewById<ImageView>(R.id.profilePhoto)

        genderText.text = "Gender: $gender"
        ageText.text = "Age: $age"
        if (photoUri != null) {
            profilePhoto.setImageURI(Uri.parse(photoUri))
        }
    }
}
