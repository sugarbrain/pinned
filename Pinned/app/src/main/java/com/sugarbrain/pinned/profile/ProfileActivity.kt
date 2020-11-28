package com.sugarbrain.pinned.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sugarbrain.pinned.PostsAdapter
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.models.User
import com.sugarbrain.pinned.submit.SubmitActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter
    private var currentUser: User? = null
    private var displayUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        loadProfile()
    }

    private fun setDisplayUser() {
        displayUser = intent.extras?.get(DISPLAY_USER_KEY) as User? ?: currentUser
    }

    private fun loadPosts() {
        firestoreDb = FirebaseFirestore.getInstance()
        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)

        // Bind the adapter and layout manager to the RV
        rvProfilePosts.adapter = adapter
        rvProfilePosts.layoutManager = LinearLayoutManager(this)

        val postsReference = firestoreDb
            .collection("posts")
            .whereEqualTo("user.arroba", displayUser?.arroba)
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

    private fun loadProfile() {
        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                currentUser = userSnapshot.toObject(User::class.java)
                setDisplayUser()
                enableLogout()
                tvUsername.text = displayUser?.name
                tvArroba.text = "@${displayUser?.arroba}"
                Log.i(TAG, displayUser!!.avatarUrl)
                Glide.with(this).load(displayUser?.avatarUrl).into(ivAvatar)
                loadPosts()
            }
            .addOnFailureListener { exception ->
                Log.i(SubmitActivity.TAG, "Fail to get current user", exception)
            }
    }

    private fun enableLogout() {
        if (currentUser == displayUser) {
            ivLogout.isVisible = true
        }
    }

    companion object {
        const val TAG = "ProfileActivity"
        const val DISPLAY_USER_KEY = "DISPLAY_USER"
    }
}