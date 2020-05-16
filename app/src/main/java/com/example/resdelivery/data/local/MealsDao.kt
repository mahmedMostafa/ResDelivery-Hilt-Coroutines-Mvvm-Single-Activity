package com.example.resdelivery.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.resdelivery.models.Meal

@Dao
interface MealsDao {

    @Insert(onConflict = IGNORE)
    fun insertMeals(meals: List<Meal>): Array<Long>

    @Insert(onConflict = REPLACE)
    fun insertMeal(meals: Meal)

    @Query("update meals set title = :title , image_url = :imageUrl , rate = :rate where id = :id")
    fun updateMeal(id: String, title: String, imageUrl: String, rate: Double)

    @Query("select * from meals")
    fun getMeals(): List<Meal>

    @Query("select * from meals where id = :mealId")
    fun getMealById(mealId: String): Meal

    @Query("select * from meals where title like '%' || :query || '%' or ingredients like '%' || :query || '%' limit (:page *30)")
    fun searchMeal(query: String, page: Int): LiveData<List<Meal>>
}