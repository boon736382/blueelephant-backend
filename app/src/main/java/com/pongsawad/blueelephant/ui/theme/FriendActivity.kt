package com.pongsawad.blueelephant.ui.theme

import android.content.Context
import android.content.Intent
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

    // Base URL for your Render backend
    private val BASE_URL = "https://blueelephant-backend.onrender.com/"

    private val prefs by lazy { getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        rvFriends = findViewById(R.id.friendsRecyclerView)
        rvFriends.layoutManager = LinearLayoutManager(this)

        // Initialize adapter
        adapter = FriendAdapter(friendList) { selectedFriend ->
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("FRIEND_NAME", selectedFriend.name)
            intent.putExtra("RECEIVER_EMAIL", selectedFriend.email)
            startActivity(intent)
        }
        rvFriends.adapter = adapter

        // Sync local profile view and load remote friends
        displayMyProfile()
        loadFriends()
    }

    private fun displayMyProfile() {
        // MUST match the keys saved in OnboardingActivity success block
        val name = prefs.getString("user_name", "Unknown User")
        val age = prefs.getString("user_age", "??")
        val gender = prefs.getString("user_gender", "Unknown")

        // This key must be saved in OnboardingActivity when the image upload finishes!
        val relativePath = prefs.getString("user_photo_path", null)

        findViewById<TextView>(R.id.nameTextView).text = name
        findViewById<TextView>(R.id.infoTextView).text = "$age years old • $gender"

        val ivMyProfile = findViewById<ImageView>(R.id.profileImageView)

        if (!relativePath.isNullOrEmpty()) {
            // Build full URL: https://.../uploads/filename.jpg
            val fullUrl = if (relativePath.startsWith("http")) relativePath else BASE_URL + relativePath

            Glide.with(this)
                .load(fullUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground) // Change to a real error icon later
                .into(ivMyProfile)
        }
    }

    private fun loadFriends() {
        val myName = prefs.getString("user_name", "")

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getAllUsers()
                if (response.isSuccessful) {
                    val serverList = response.body() ?: emptyList()

                    val mappedList = serverList
                        .filter { it.name != myName && !it.name.isNullOrEmpty() }
                        .map { u ->
                            // Ensure the image path is converted to a full URL
                            val rawPath = u.profile_image
                            val friendImageUrl = when {
                                rawPath.isNullOrEmpty() -> ""
                                rawPath.startsWith("http") -> rawPath
                                else -> BASE_URL + rawPath
                            }

                            Friend(
                                id = u.id.toString(),
                                name = u.name ?: "Unknown Friend",
                                email = u.email ?: "",
                                status = "Offline",
                                imageUrl = friendImageUrl
                            )
                        }

                    Log.d("FRIEND_DEBUG", "Loaded ${mappedList.size} friends")
                    adapter.updateData(mappedList)
                } else {
                    Log.e("API_ERROR", "Failed to load users: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Check connection: ${e.message}")
            }
        }
    }
}