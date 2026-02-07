package com.pongsawad.blueelephant.ui.theme

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.Friend
import com.pongsawad.blueelephant.FriendAdapter
import com.pongsawad.blueelephant.R
import com.pongsawad.blueelephant.network.ApiClient // Import your existing ApiClient
import kotlinx.coroutines.launch

class FriendActivity : AppCompatActivity() {

    private lateinit var rvFriends: RecyclerView
    private val friendsList = mutableListOf<Friend>()
    private lateinit var adapter: FriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        rvFriends = findViewById(R.id.rv_friends)

        // Initialize adapter with empty list first
        adapter = FriendAdapter(friendsList) { friend ->
            // Handle click
        }

        rvFriends.adapter = adapter
        rvFriends.layoutManager = LinearLayoutManager(this)

        // CALL THE DATABASE DATA HERE
        loadFriends()
    }

    private fun loadFriends() {
        lifecycleScope.launch {
            try {
                val list = ApiClient.apiService.getFriends()
                if (list.isNotEmpty()) {
                    // We must update the list and notify the adapter
                    friendsList.clear()
                    friendsList.addAll(list)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "Failed: ${e.message}")
            }
        }
    }
}