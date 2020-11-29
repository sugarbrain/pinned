package com.sugarbrain.pinned.search

import android.content.Context
import android.content.Intent
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.models.Post
import com.sugarbrain.pinned.models.User
import com.sugarbrain.pinned.profile.ProfileActivity
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_post.view.*

class ContactsAdapter(val context: Context, private val users: List<User>) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            itemView.tvContactName.text = user.name
            itemView.setOnClickListener {
                val profileIntent = Intent(context, ProfileActivity::class.java)
                profileIntent.putExtra(ProfileActivity.DISPLAY_USER_KEY, user)
                context.startActivity(profileIntent)
            }
        }
    }
}
