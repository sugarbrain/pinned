package com.sugarbrain.pinned.feed

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sugarbrain.pinned.PostsAdapter
import android.widget.Toast
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.search.SearchActivity
import com.sugarbrain.pinned.submit.SubmitActivity
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;

    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        createSearchEditTextListener()
        loadPosts()
        createCameraButtonListener()
    }

    private fun loadPosts() {
        firestoreDb = FirebaseFirestore.getInstance()
        posts = mutableListOf()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val image = data?.extras?.get("data") as Bitmap
                Log.i(TAG, "image captured -> $image")
                val submitIntent = Intent(this, SubmitActivity::class.java);
                submitIntent.putExtra(SubmitActivity.SUBMIT_IMAGE_KEY, image)
                startActivity(submitIntent)
            } else {
                Toast.makeText(this, "It was not possible to capture the photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createCameraButtonListener() {
        btCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
            }
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

    companion object {
        var IMAGE_CAPTURE_CODE = 666
        var TAG = "FeedActivity"
    }
}