package com.example.resdelivery.network.responses

import com.example.resdelivery.models.Meal
import com.squareup.moshi.Json

data class MealResponse(
    @Json(name = "recipe")
    var meal : Meal
)