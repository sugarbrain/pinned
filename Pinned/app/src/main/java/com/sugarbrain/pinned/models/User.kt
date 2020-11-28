package com.sugarbrain.pinned.models

import com.google.firebase.firestore.PropertyName

data class User(
    var name: String = "",
    var arroba: String = "",

    @get:PropertyName("avatar_url") @set:PropertyName("avatar_url")
    var avatarUrl: String = ""
)