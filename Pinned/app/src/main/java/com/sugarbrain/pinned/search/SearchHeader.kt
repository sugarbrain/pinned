package com.sugarbrain.pinned.search

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.models.User
import com.sugarbrain.pinned.profile.ProfileActivity
import com.sugarbrain.pinned.submit.SubmitActivity
import kotlinx.android.synthetic.main.search_header.view.*


class SearchHeader(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var firestoreDb: FirebaseFirestore
    private var currentUser: User? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.search_header, this, true)

        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                currentUser = userSnapshot.toObject(User::class.java)
                Glide.with(this).load(currentUser?.avatarUrl).into(profile_image)
            }
            .addOnFailureListener { exception ->
                Log.i(SubmitActivity.TAG, "Fail to get current user", exception)
            }

        setProfileImageOnClickListener()
    }

    private fun setProfileImageOnClickListener() {
        profile_image.setOnClickListener {
            val profileIntent = Intent(context, ProfileActivity::class.java)
            context.startActivity(profileIntent)
        }
    }
}