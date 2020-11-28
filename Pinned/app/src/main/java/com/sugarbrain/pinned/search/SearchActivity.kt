package com.sugarbrain.pinned.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.feed.FeedActivity
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;
    private lateinit var contacts: MutableList<Contact>
    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        focusOnSearchEditText()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                listOf(Manifest.permission.READ_CONTACTS).toTypedArray(),
                PERMISSIONS_REQUEST_READ_CONTACTS
            );
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(this, "Unable to get contacts permission", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, FeedActivity::class.java))
            }
        }
    }

    private fun focusOnSearchEditText() {
        searchEditText = findViewById(R.id.header_search_edit_text)
        searchEditText.requestFocus()
        val keyboard: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private fun loadContacts() {
        contacts = mutableListOf()
        adapter = ContactsAdapter(this, contacts)

        rvContacts.adapter = adapter
        rvContacts.layoutManager = LinearLayoutManager(this)

        val contentResolver = contentResolver
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor!!.moveToFirst()) {
            while (cursor.moveToNext()) {
                val contactName =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (contactName != null) {
                    contacts.add(Contact(contactName))
                }
            }
            adapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "It was not possible to load the contacts", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    }
}