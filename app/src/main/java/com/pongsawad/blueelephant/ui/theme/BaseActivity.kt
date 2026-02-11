package com.pongsawad.blueelephant

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Look in "SETTINGS" to see if the user picked "th" or "en"
        val prefs = newBase.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val lang = prefs.getString("MY_LANG", "en") ?: "en"

        // Set the language for the whole app context
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        // Inject this language setting into the Activity
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}