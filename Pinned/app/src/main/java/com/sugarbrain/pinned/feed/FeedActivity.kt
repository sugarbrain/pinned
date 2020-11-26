package com.sugarbrain.pinned.feed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sugarbrain.pinned.PostsAdapter
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.search.SearchActivity
import kotlinx.android.synthetic.main.activity_feed.*

private const val TAG = "FeedActivity"

class FeedActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        createSearchEditTextListener()


        firestoreDb = FirebaseFirestore.getInstance()

        // Create the layout file which represents one post - DONE
        // Create data source - DONE
        posts = mutableListOf()
        // Create the adapter
        adapter = PostsAdapter(this, posts)

        // Bind the adapter and layout manager to the RV
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        val postsReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("date", Query.Direction.DESCENDING)

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }

            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun createSearchEditTextListener() {
        searchEditText = findViewById(R.id.header_search_edit_text)
        searchEditText.setOnFocusChangeListener { _: View, b: Boolean ->
            val searchIntent = Intent(this, SearchActivity::class.java);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            if (b) startActivity(searchIntent);
        }
    }
}