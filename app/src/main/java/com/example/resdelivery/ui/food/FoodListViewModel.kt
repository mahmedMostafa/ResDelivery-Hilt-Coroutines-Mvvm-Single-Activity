package com.example.resdelivery.ui.food

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.models.Meal
import com.example.resdelivery.network.requests.ApiService
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception


enum class FoodApiStatus { LOADING, SUCCESS, ERROR }

class FoodListViewModel(
    private val foodApi: ApiService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sessionManagement: SessionManagement
) : ViewModel() {

    private val _status = MutableLiveData<FoodApiStatus>()
    private val _foodList = MutableLiveData<List<Meal>>()

    val status: LiveData<FoodApiStatus>
        get() = _status

    val foodList: LiveData<List<Meal>>
        get() = _foodList

    private var job = Job()
    private val coroutineStatus = CoroutineScope(job + Main)


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getFood() {
        Timber.d("getFood gets called")
        coroutineStatus.launch {
            val foodDeferred = foodApi.getFood("Breakfast")
            try {
                _status.value = FoodApiStatus.LOADING
                //this will run on a thread managed by retrofit
                val foodList = foodDeferred.await()
                _status.value = FoodApiStatus.SUCCESS
                _foodList.value = foodList.meals
                Timber.e("That is a success " + foodList.meals.size.toString())
            } catch (e: Exception) {
                Timber.e(e.message)
                _status.value = FoodApiStatus.ERROR
                _foodList.value = ArrayList()
            }
        }
    }

    fun getLastKnownLocation() {
        Timber.d("getLastKnownLocation Called")
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            Timber.d("I'm in the task")
            if (task.isSuccessful) {
                Timber.d("I'm in the task successful")
                //the result sometimes returns null and idk why
                task.result?.let {
                    val location: Location = it
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    Timber.d("complete Latitude is  ${geoPoint.latitude}")
                    Timber.d("complete Longitude is  ${geoPoint.longitude}")
                    sessionManagement.setUserLocation(
                        geoPoint.latitude.toString(),
                        geoPoint.longitude.toString()
                    )
                    Timber.d(sessionManagement.getUserLatitude().toString())
                    Timber.d(sessionManagement.getUserLongitude().toString())
                }
                if (task.result == null) {
                    Timber.d("it returns null")
                    sessionManagement.setUserLocation("31", "30")
                }
            } else {
                Timber.e(task.exception?.message)
                sessionManagement.setUserLocation(
                    "31",
                    "30"
                )
            }
        }
//        val locationWork = OneTimeWorkRequestBuilder<LocationWork>()
//            .build()
//        WorkManager.getInstance(activity!!).enqueue(locationWork)
    }

    fun logOutUser() {
        sessionManagement.clearUser()
    }
}