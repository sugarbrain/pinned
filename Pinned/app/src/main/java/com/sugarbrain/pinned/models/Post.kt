package com.sugarbrain.pinned.models
import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    var date: Long = 0,
    var user: User? = null,

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = ""
)
