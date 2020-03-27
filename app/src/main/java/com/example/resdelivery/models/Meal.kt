package com.example.resdelivery.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meal(
    @Json(name = "recipe_id")
    var id: String = "",

    var title: String = "",

    @Json(name = "image_url")
    var imageUrl: String = "",

    @Json(name = "social_rank")
    var rate: Double = 0.0,

    //b/c this only exists in the second api request so begin with empty array
    @Json(name = "ingredients")
    var ingredients: Array<String> = arrayOf()
) : Parcelable