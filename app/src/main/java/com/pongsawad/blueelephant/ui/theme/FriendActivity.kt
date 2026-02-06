import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pongsawad.blueelephant.R

class FriendsActivity : AppCompatActivity() {

    private lateinit var rvFriends: RecyclerView
    private val friends = mutableListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        rvFriends = findViewById(R.id.rv_friends)
        val adapter = FriendAdapter(friends) { friend ->
            // Open chat with selected friend
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("friendId", friend.id)
            intent.putExtra("friendName", friend.name)
            startActivity(intent)
        }

        rvFriends.adapter = adapter
        rvFriends.layoutManager = LinearLayoutManager(this)

        // TODO: Load friends from backend
        friends.add(Friend("1", "Alice"))
        friends.add(Friend("2", "Bob"))
        adapter.notifyDataSetChanged()
    }
}
