package com.example.resdelivery.ui.food

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resdelivery.models.Meal
import com.example.resdelivery.data.network.requests.ApiService
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class FoodListViewModel(
    private val repository: FoodListRepository
) : ViewModel() {

    val status: LiveData<FoodApiStatus>
        get() = repository.status

    val foodList: LiveData<List<Meal>>
        get() = repository.foodList


    fun refreshMeals() = viewModelScope.launch {
        repository.refreshMeals()
    }

    fun getLastKnownLocation () {
        repository.getLastKnownLocation()
    }

    fun logOutUser() {
        repository.logOutUser()
    }

}