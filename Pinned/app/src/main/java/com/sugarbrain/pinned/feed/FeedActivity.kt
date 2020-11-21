package com.sugarbrain.pinned.feed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.search.SearchActivity

class FeedActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        createSearchEditTextListener()
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