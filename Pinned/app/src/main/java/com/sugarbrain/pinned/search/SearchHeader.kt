package com.sugarbrain.pinned.search

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.sugarbrain.pinned.R
import com.sugarbrain.pinned.profile.ProfileActivity
import kotlinx.android.synthetic.main.search_header.view.*


class SearchHeader(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.search_header, this, true)
        setProfileImageOnClickListener()
    }

    private fun setProfileImageOnClickListener() {
        profile_image.setOnClickListener {
            val profileIntent = Intent(context, ProfileActivity::class.java)
            context.startActivity(profileIntent)
        }
    }
}