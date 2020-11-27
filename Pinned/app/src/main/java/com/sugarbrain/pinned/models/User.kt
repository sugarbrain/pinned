package com.sugarbrain.pinned.models

import com.google.firebase.firestore.PropertyName

data class User(
    var name: String = "",
    var arroba: String = "",

    @get:PropertyName("avatar_url") @set:PropertyName("image_url")
    var avatarUrl: String = ""
)