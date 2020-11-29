package com.sugarbrain.pinned.submit

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.feed.FeedActivity
import com.sugarbrain.pinned.models.Place
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.models.User
import kotlinx.android.synthetic.main.activity_submit.*
import java.io.ByteArrayOutputStream

class SubmitActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var currentUser: User? = null
    private var place: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)
        setSubmitImage()
        setPlace()
        setCurrentUser()
        btSubmit.setOnClickListener { handleSubmitButtonClick() }
    }

    private fun setSubmitImage() {
        ivSubmitImage.setImageBitmap(getCapturedImage())
    }

    private fun setPlace() {
        place = intent.extras?.get(PLACE_KEY) as Place?
        if (place != null) {
            tvPlaceName.text = place?.name
            tvPlaceAddress.text = place?.address
        }
    }

    private fun setCurrentUser() {
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                currentUser = userSnapshot.toObject(User::class.java)
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Fail to get current user", exception)
            }
    }

    private fun handleSubmitButtonClick() {
        if (getCapturedImage() == null) {
            Toast.makeText(this, "Couldn't get captured image", Toast.LENGTH_SHORT).show()
            return
        }
        if (etSubmitDescription.text.isBlank()) {
            Toast.makeText(this, "Description is empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentUser == null) {
            Toast.makeText(this, "No user is signed", Toast.LENGTH_SHORT).show()
            return
        }
        submitPost()
    }

    private fun submitPost() {
        btSubmit.isEnabled = false

        storageReference = FirebaseStorage.getInstance().reference

        val photoReference =
            storageReference.child("images/${currentUser?.arroba}-${System.currentTimeMillis()}-photo.jpeg")
        photoReference.putBytes(getSubmitImageByteArray())
            .continueWithTask {
                Log.i(TAG, "uploaded -> ${it.result?.bytesTransferred}")
                photoReference.downloadUrl
            }
            .continueWithTask {
                val post = Post(
                    etSubmitDescription.text.toString(),
                    System.currentTimeMillis(),
                    currentUser,
                    it.result.toString(),
                    place
                )
                firestore.collection("posts").add(post)
            }
            .addOnCompleteListener {
                btSubmit.isEnabled = true
                if (!it.isSuccessful) {
                    Log.i(TAG, "Fail to save post", it.exception)
                    Toast.makeText(this, "Fail to save post", Toast.LENGTH_SHORT).show()
                }
                etSubmitDescription.text.clear()
                ivSubmitImage.setImageResource(0)
                Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show()
                val feedIntent = Intent(this, FeedActivity::class.java)
                startActivity(feedIntent)
                finish()
            }
    }

    private fun getSubmitImageByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        getCapturedImage()?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun getCapturedImage() = intent.extras?.get(SUBMIT_IMAGE_KEY) as Bitmap?

    companion object {
        val TAG = "SubmitActivity"
        val SUBMIT_IMAGE_KEY = "SUBMIT_IMAGE"
        val PLACE_KEY = "SUBMIT_PLACE"
    }
}