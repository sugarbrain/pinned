package com.sugarbrain.pinned.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.sugarbrain.pinned.R


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        focusOnSearchEditText()
    }

    private fun focusOnSearchEditText() {
        searchEditText = findViewById(R.id.header_search_edit_text)
        searchEditText.requestFocus()
        val keyboard: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}