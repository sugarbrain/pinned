package com.sugarbrain.pinned.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.feed.FeedActivity
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.models.User
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText;
    private lateinit var contacts: MutableList<User>
    private lateinit var adapter: ContactsAdapter
    private lateinit var firestoreDb: FirebaseFirestore

    private var allContactPhones: MutableList<String> = mutableListOf()

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
            displayContacts()
        }

        searchEditText.addTextChangedListener {text ->
            if (!text.isNullOrEmpty()) {
                contacts = contacts.filter {
                    it.name.toLowerCase().startsWith(text.toString().toLowerCase())
                }.toMutableList()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val keyboard: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(view.windowToken, 0)
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
                displayContacts()
            } else {
                Toast.makeText(this, "Unable to get contacts permission", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, FeedActivity::class.java))
            }
        }
    }

    private fun displayContacts() {
        firestoreDb = FirebaseFirestore.getInstance()
        val usersReference = firestoreDb
            .collection("users")

        usersReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying users", exception)
                return@addSnapshotListener
            }

            val users = snapshot.toObjects(User::class.java)

            loadContacts()

            users.filter {
                allContactPhones.contains(it.phone)
            }.forEach {
                contacts.add(it)
            }

            adapter.notifyDataSetChanged()
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
                val contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val phones =
                    contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null
                    );
                while (phones!!.moveToNext()) {
                    var number =
                        phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    number = number.replace("+", "")
                    number = number.replace("-", "")
                    number = number.replace(" ", "")
                    number = number.takeLast(8)
                    Log.i(TAG, number)
                    allContactPhones.add(number)
                }
            }
        } else {
            Toast.makeText(this, "It was not possible to load the contacts", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val TAG = "SearchActivity"
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    }
}