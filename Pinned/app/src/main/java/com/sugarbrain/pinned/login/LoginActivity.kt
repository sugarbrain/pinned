package com.sugarbrain.pinned.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.feed.FeedActivity
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            goToFeedActivity()
        }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false

            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/senha devem ser preenchidos", Toast.LENGTH_SHORT).show()
                loginButton.isEnabled = true
                return@setOnClickListener
            }

            // Firebase authentication check
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                loginButton.isEnabled = true

                if (task.isSuccessful) {
                    goToFeedActivity()
                } else {
                    Log.e(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Erro na autenticação", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToFeedActivity() {
        Log.i(TAG, "goToFeedActivity")
        startActivity(Intent(this, FeedActivity::class.java))
        finish()
    }
}