package com.example.resdelivery.ui.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.resdelivery.models.Meals
import com.example.resdelivery.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

@ExperimentalCoroutinesApi
class FoodListViewModel(
    private val repository: FoodListRepository
) : ViewModel() {

    val meals: LiveData<Result<Meals>> = repository
        .refreshMeals()
        .onStart { emit(Result.Loading) }
        .asLiveData()

    fun getLastKnownLocation() {
        repository.getLastKnownLocation()
    }

    fun logOutUser() {
        repository.logOutUser()
    }

}