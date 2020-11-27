package com.sugarbrain.pinned

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import android.content.Intent
import android.view.LayoutInflater
import com.sugarbrain.pinned.feed.FeedActivity
import kotlinx.android.synthetic.main.bottom_navigation.view.*

class BottomNavigation(context: Context, attrs: AttributeSet?): ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.bottom_navigation, this, true)
        setupNavigation()
    }

    private fun setupNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.bottom_home -> {
                    goToFeedActivity()
                    true
                }
                R.id.bottom_checkin -> {
                    goToCheckInActivity()
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener { }
    }

    private fun goToFeedActivity() {
        context.startActivity(Intent(context, FeedActivity::class.java))
    }

    private fun goToCheckInActivity() {
//        context.startActivity(Intent(context, SubmitActivity::class.java))
    }
}