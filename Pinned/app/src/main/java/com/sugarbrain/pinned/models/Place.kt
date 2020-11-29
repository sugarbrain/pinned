package com.sugarbrain.pinned.models

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Place (
    var name: String = "",
    var address: String = "",
    var id: String = ""
) : Serializable
