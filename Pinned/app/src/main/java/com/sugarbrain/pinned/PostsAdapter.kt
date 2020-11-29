package com.sugarbrain.pinned

import android.content.Context
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sugarbrain.pinned.models.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostsAdapter (val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            val desc = context.getString(
                R.string.post_description,
                post.user?.arroba,
                post.description
            )

            itemView.localeText.text = post.place?.name
            itemView.tvDescription.text = desc
            itemView.tvDate.text = DateUtils.getRelativeTimeSpanString(post.date)
            Glide.with(context).load(post.imageUrl).into(itemView.imageView)
        }
    }
}
