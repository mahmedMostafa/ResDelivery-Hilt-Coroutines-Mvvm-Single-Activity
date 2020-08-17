package com.example.resdelivery.features.food.domain

import android.annotation.SuppressLint
import android.location.Location
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.Meals
import com.example.resdelivery.util.Result
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FoodListRepository @Inject constructor(
    private val foodApi: ApiService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sessionManagement: SessionManagement,
    private val mealsDao: MealsDao
) {

    fun refreshMeals(page: Int): Flow<Result<ArrayList<Meal>>> {
        return flow {
            emit(Result.Loading)
            try {
                val meals = foodApi.getFood("Bacon", page).meals as ArrayList
                updateCache(meals)
                emit(Result.Success(meals))
            } catch (e: Exception) {
                Timber.e("Gemy error is ${e.message}")
                //TODO make the error
                emit(Result.Success(mealsDao.getMeals() as ArrayList))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun updateCache(meals: Meals) {
        for ((i, id) in mealsDao.insertMeals(meals).withIndex()) {
            Timber.d("$i")
            if (id == -1L) {
                mealsDao.updateMeal(
                    meals[i].id,
                    meals[i].title,
                    meals[i].imageUrl,
                    meals[i].rate
                )
            }
        }
    }

    suspend fun refreshMealsInBackground() {
        try {
            val meals = foodApi.getFood("Breakfast", 2).meals
            updateCache(meals)
        } catch (e: java.lang.Exception) {
            Timber.e("Couldn't refresh meals")
            Timber.e(e)
        }
    }

    @SuppressLint("MissingPermission") // will be added later
    fun getLastKnownLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //the result sometimes returns null and idk why
                task.result?.let {
                    val location: Location = it
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    sessionManagement.setUserLocation(
                        geoPoint.latitude.toString(),
                        geoPoint.longitude.toString()
                    )
                    Timber.d(sessionManagement.getUserLatitude().toString())
                    Timber.d(sessionManagement.getUserLongitude().toString())
                }
                if (task.result == null) {
                    sessionManagement.setUserLocation("31", "30")
                }
            } else {
                Timber.e(task.exception)
                sessionManagement.setUserLocation(
                    "31",
                    "30"
                )
            }
        }
    }

    fun logOutUser() {
        sessionManagement.clearUser()
    }
}