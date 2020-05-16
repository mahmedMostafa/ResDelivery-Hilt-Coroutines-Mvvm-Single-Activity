package com.example.resdelivery.data.network.responses

import com.example.resdelivery.models.Meal
import com.squareup.moshi.Json

data class FoodListResponse(

    val count : Int,
    @Json(name = "recipes")
    var meals : List<Meal>
)