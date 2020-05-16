package com.example.resdelivery.ui.food

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.requests.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

enum class FoodApiStatus { LOADING, SUCCESS, ERROR }

class FoodListRepository(
    private val foodApi: ApiService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sessionManagement: SessionManagement,
    private val mealsDao: MealsDao
) {

    private val _status = MutableLiveData<FoodApiStatus>()
    private val _foodList = MutableLiveData<List<Meal>>()

    val status: LiveData<FoodApiStatus>
        get() = _status

    val foodList: LiveData<List<Meal>>
        get() = _foodList

    suspend fun refreshMeals() = withContext(Dispatchers.IO) {
        val meals: List<Meal>
        Timber.d("getFood gets called")
        val foodDeferred = foodApi.getFood("Breakfast")
        try {
            _status.postValue(FoodApiStatus.LOADING)
            //this will run on a thread managed by retrofit like enqueue
            val foodList = foodDeferred.await()
            meals = foodList.meals
            for ((i, id) in mealsDao.insertMeals(meals).withIndex()) {
                Timber.d("$i")
                if (id == -1L) {
                    Timber.d("CONFLICT this meals is already in the cache")
                    mealsDao.updateMeal(
                        meals[i].id,
                        meals[i].title,
                        meals[i].imageUrl,
                        meals[i].rate
                    )
                } else {
                    Timber.d("New Meal added to the cache")
                }
            }
            _status.postValue(FoodApiStatus.SUCCESS)
            _foodList.postValue(meals)
        } catch (e: Exception) {
            Timber.e(e)
            _status.postValue(FoodApiStatus.ERROR)
            _foodList.postValue(mealsDao.getMeals())
        }
    }

    suspend fun refreshMealsInBackground() {
        val meals: List<Meal>
        val foodDeferred = foodApi.getFood("Breakfast")
        try {
            val foodList = foodDeferred.await()
            meals = foodList.meals
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