package com.sugarbrain.pinned

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.sugarbrain.pinned.feed.FeedActivity

class MainActivity : AppCompatActivity() {

    private lateinit var feedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createFeedButtonListener()
    }

    private fun createFeedButtonListener() {
        feedButton = findViewById(R.id.main_feed_button)
        feedButton.setOnClickListener {
            val feedIntent = Intent(this, FeedActivity::class.java);
            startActivity(feedIntent);
        }
    }
}