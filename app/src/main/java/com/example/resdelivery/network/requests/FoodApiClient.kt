package com.example.resdelivery.network.requests

import com.example.resdelivery.network.responses.FoodListResponse
import com.example.resdelivery.network.responses.MealResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("api/search")
    fun getFood(
        @Query("q") query: String
    ):
            Deferred<FoodListResponse>

    @GET("api/get")
    fun getMeal(
        @Query("rId") mealId: String
    ):
            Deferred<MealResponse>

}