package com.example.resdelivery.features.food.representation

import androidx.lifecycle.*
import com.example.resdelivery.features.food.domain.FoodListRepository
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.Meals
import com.example.resdelivery.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class FoodListViewModel(
    private val repository: FoodListRepository
) : ViewModel() {

    var pageNumber = 1
    var reachedLastMeals = false

    private val _meals = MutableLiveData<ArrayList<Meal>>()
    val meals: LiveData<ArrayList<Meal>> get() = _meals

    private val _loading = MutableLiveData(true)
    private val _paginationLoading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading
    val paginationLoading: LiveData<Boolean> get() = _paginationLoading

    init {
        getMeals(pageNumber)
    }

    fun getMoreMeals() {
        if (!reachedLastMeals) {
            pageNumber++
            Timber.d("Current page number is $pageNumber")
            getMeals(pageNumber)
        } else {
            _loading.value = false
            _paginationLoading.value = false
        }
    }

    fun getMeals(pageNumber: Int) = viewModelScope.launch {
        Timber.d("Gemy Meals Called")
        repository.refreshMeals(pageNumber)
//            .onStart { _loading.value = true }
            .catch { Timber.e("Error from flows $it") }
            .collect {
                if (it is Result.Loading) {
                    //if it's the first page then show the refresh loading not the pagination loading
                    if (pageNumber == 1) {
                        _loading.value = true
                    } else {
                        _paginationLoading.value = true
                    }
                } else {
                    Timber.d("Current Result is ${it}")
                    val currentMeals = (it as Result.Success).data
                    //if it's the first page hide the refresh loading and set the list
                    if (pageNumber == 1) {
                        _loading.value = false
                        _meals.value = currentMeals
                    } else {
                        //if not we hide the pagination loading and add the list to the existing one
                        if (currentMeals.isEmpty()) {
                            reachedLastMeals = true
                            _paginationLoading.value = false
                        } else {
                            _paginationLoading.value = false
                            val oldMeals: ArrayList<Meal> = _meals.value ?: arrayListOf()
                            oldMeals.addAll(currentMeals)
                            _meals.value = oldMeals
                        }
                    }
                }
            }
    }

    fun getLastKnownLocation() {
        repository.getLastKnownLocation()
    }

    fun logOutUser() {
        repository.logOutUser()
    }

}