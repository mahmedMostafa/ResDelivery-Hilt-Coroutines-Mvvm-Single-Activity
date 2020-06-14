package com.example.resdelivery.ui.food

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.requests.ApiService
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
import kotlinx.coroutines.withContext
import timber.log.Timber

@ExperimentalCoroutinesApi
class FoodListRepository(
    private val foodApi: ApiService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sessionManagement: SessionManagement,
    private val mealsDao: MealsDao
) {

    fun refreshMeals(): Flow<Result<Meals>> {
        return flow {
            try {
                val meals = foodApi.getFood("Breakfast").meals
                updateCache(meals)
                emit(Result.Success(meals))
            } catch (e: Exception) {
                emit(Result.Error(mealsDao.getMeals()))
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
            val meals = foodApi.getFood("Breakfast").meals
            updateCache(meals)
        } catch (e: java.lang.Exception) {
            Timber.e("Couldn't refresh meals")
            Timber.e(e)
        }
    }

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