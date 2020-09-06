package com.example.resdelivery.features.food.representation

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.resdelivery.features.food.domain.FoodListRepository
import com.example.resdelivery.models.Meal
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
class FoodListViewModel @ViewModelInject constructor(
    private val repository: FoodListRepository,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {


    private var currentQuery: String? = null

    private var currentMeals: Flow<PagingData<Meal>>? = null

    private val _meals = MutableLiveData<PagingData<Meal>>()
    val meals: LiveData<PagingData<Meal>> get() = _meals

    fun searchMeals(query: String): Flow<PagingData<Meal>> {
        val lastMeals = currentMeals
        if (query == currentQuery && lastMeals != null) {
            return lastMeals
        }
        currentQuery = query
        val newResult = repository.getMealsPage(query).cachedIn(viewModelScope)
        currentMeals = newResult
        return newResult
    }

    fun getMeals(query: String) = viewModelScope.launch {
        if (query == currentQuery) return@launch
        currentQuery = query
        repository.getMealsPage(query).cachedIn(viewModelScope).collect {
            _meals.value = it
        }
    }

    fun logOutUser() {
        repository.logOutUser()
    }
}