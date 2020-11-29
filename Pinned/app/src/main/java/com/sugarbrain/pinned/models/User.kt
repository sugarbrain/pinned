package com.sugarbrain.pinned.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class User(
    var name: String = "",
    var arroba: String = "",
    var phone: String? = "",

    @get:PropertyName("avatar_url") @set:PropertyName("avatar_url")
    var avatarUrl: String = ""
) : Serializable