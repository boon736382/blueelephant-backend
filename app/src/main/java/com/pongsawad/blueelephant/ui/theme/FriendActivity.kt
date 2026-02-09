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
        val name = prefs.getString("PROFILE_NAME", "User Name")
        val age = prefs.getString("PROFILE_AGE", "??")
        val gender = prefs.getString("PROFILE_GENDER", "Unknown")

        // Use the URL we saved from Firebase in Onboarding
        val photoUrl = prefs.getString("PROFILE_PHOTO_URL", null)

        val tvMyName = findViewById<TextView>(R.id.tv_my_name)
        val tvMyInfo = findViewById<TextView>(R.id.tv_my_info)
        val ivMyProfile = findViewById<ImageView>(R.id.iv_my_profile)

        tvMyName.text = name
        tvMyInfo.text = "$age years old • $gender"

        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background) // Fallback while loading
                .into(ivMyProfile)
        }
    }


    private fun loadFriends() {
        val myName = prefs.getString("PROFILE_NAME", "")

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getAllUsers()
                if (response.isSuccessful) {
                    val serverList = response.body()
                    if (serverList != null) {
                        val mappedList = serverList
                            .filter { it.name != myName }
                            .map { u ->
                                Friend(
                                    id = u.id.toString(),
                                    name = u.name,
                                    email = u.email,
                                    status = "Online",
                                    imageUrl = u.profile_image // Ensure your User model has this field
                                )
                            }
                        // ✅ IMPORTANT: Update the adapter with the new list
                        adapter.updateData(mappedList)
                    }
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "${e.message}")
            }
        }
    }
}