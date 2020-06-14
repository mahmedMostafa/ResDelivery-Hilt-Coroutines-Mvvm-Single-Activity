package com.example.resdelivery.data.network.requests

import com.example.resdelivery.data.network.responses.FoodListResponse
import com.example.resdelivery.data.network.responses.MealResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("api/search")
    suspend fun getFood(
        @Query("q") query: String
    ): FoodListResponse

    @GET("api/get")
    suspend fun getMeal(
        @Query("rId") mealId: String
    ): MealResponse

}