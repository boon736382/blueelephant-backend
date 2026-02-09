package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pongsawad.blueelephant.ChatRoomActivity
import com.pongsawad.blueelephant.Friend
import com.pongsawad.blueelephant.FriendAdapter
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient
import kotlinx.coroutines.launch

class FriendActivity : AppCompatActivity() {

    private lateinit var rvFriends: RecyclerView
    private lateinit var adapter: FriendAdapter
    private var friendList = mutableListOf<Friend>()

    private val prefs by lazy { getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        rvFriends = findViewById(R.id.rv_friends)
        rvFriends.layoutManager = LinearLayoutManager(this)

        displayMyProfile()

        // Initialize with empty list first
        adapter = FriendAdapter(friendList) { selectedFriend ->
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("FRIEND_NAME", selectedFriend.name)
            intent.putExtra("FRIEND_ID", selectedFriend.id)
            startActivity(intent)
        }

        rvFriends.adapter = adapter
        loadFriends()
    }

    private fun displayMyProfile() {
        // 1. MATCH THE KEYS used in Onboarding/Login
        val name = prefs.getString("user_name", "User Name")
        val age = prefs.getString("user_age", "??")
        val gender = prefs.getString("user_gender", "Unknown")

        // 2. IMAGE PATH: Prefix the path with your Render URL
        val serverBaseUrl = "https://blueelephant-backend.onrender.com/"
        val relativePath = prefs.getString("user_photo_path", null)

        findViewById<TextView>(R.id.tv_my_name).text = name
        findViewById<TextView>(R.id.tv_my_info).text = "$age years old • $gender"

        if (!relativePath.isNullOrEmpty()) {
            val fullUrl = if (relativePath.startsWith("http")) relativePath else serverBaseUrl + relativePath
            Glide.with(this)
                .load(fullUrl)
                .circleCrop()
                .into(findViewById<ImageView>(R.id.iv_my_profile))
        }
    }

    private fun loadFriends() {
        // Make sure this matches what was saved during login/onboarding!
        val myName = prefs.getString("user_name", "")
        val serverBaseUrl = "https://blueelephant-backend.onrender.com/"

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getAllUsers()
                if (response.isSuccessful) {
                    val serverList = response.body() ?: emptyList()

                    val mappedList = serverList
                        .filter { it.name != myName }
                        .map { u ->
                            // Build the full image URL for each friend
                            val friendImageUrl = if (u.profile_image?.startsWith("http") == true) {
                                u.profile_image
                            } else {
                                serverBaseUrl + u.profile_image
                            }

                            Friend(
                                id = u.id.toString(),
                                name = u.name ?: "Unknown",
                                email = u.email ?: "",
                                status = "Online",
                                imageUrl = friendImageUrl // Now Glide can load it
                            )
                        }
                    adapter.updateData(mappedList)
                } else {
                    Log.e("API_ERROR", "Code: ${response.code()} Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Failed to fetch: ${e.message}")
            }
        }
    }
}