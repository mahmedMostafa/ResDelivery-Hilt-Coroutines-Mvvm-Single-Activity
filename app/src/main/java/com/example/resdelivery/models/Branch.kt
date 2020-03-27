package com.example.resdelivery.models

import com.google.firebase.firestore.GeoPoint

data class Branch(
    var location: GeoPoint? = null,
    var title : String? = ""
)