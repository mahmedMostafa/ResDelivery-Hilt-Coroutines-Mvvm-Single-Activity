package com.example.resdelivery.features.food.data

import androidx.paging.PagingSource
import com.example.resdelivery.data.network.ApiService
import com.example.resdelivery.models.Meal
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

private const val API_STARTING_INDEX = 1

//this is the class we will use if we are gonna work with network only
class MealsPagingSource(
    private val service: ApiService,
    private val query: String
) : PagingSource<Int, Meal>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Meal> {
        val page = params.key ?: API_STARTING_INDEX
        return try {
//            delay(5000)
            val result = service.getFood(query, page, params.loadSize)
            LoadResult.Page(
                data = result.meals,
                prevKey = null,
                nextKey = if (result.meals.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}