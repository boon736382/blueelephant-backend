package com.pongsawad.blueelephant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make sure the layout exists
        val layoutId = resources.getIdentifier("activity_main", "layout", packageName)
        if (layoutId != 0) {
            setContentView(layoutId)
        } else {
            // If layout is missing, log error and finish activity
            finish()
            return
        }

        // Safely find views
        val welcomeText = findViewById<TextView?>(R.id.welcomeText)
        val logoutBtn = findViewById<Button?>(R.id.logoutBtn)

        // Set welcome text if TextView exists
        welcomeText?.text = "Welcome to Blue Elephant!"

        // Set logout button click listener safely
        logoutBtn?.setOnClickListener {
            // TODO: clear login session if needed

            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
