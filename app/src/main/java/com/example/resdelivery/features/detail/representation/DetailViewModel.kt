package com.example.resdelivery.features.detail.representation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.resdelivery.features.detail.domain.DetailRepository
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.Result
import com.example.resdelivery.util.Result.Loading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class DetailViewModel @ViewModelInject constructor(
    private val repository: DetailRepository
) : ViewModel() {

    private val _loadingMeal = MutableLiveData(true)
    val loadingMeal: LiveData<Boolean> get() = _loadingMeal

    private val _addingToCart = MutableLiveData(false)
    private val _cartText = MutableLiveData("Add to Cart")
    val addingToCart: LiveData<Boolean> get() = _addingToCart
    val cartText: LiveData<String> get() = _cartText

    private val _meal = MutableLiveData<Meal>()
    val meal: LiveData<Meal> get() = _meal

    fun getMeal(id: String) = viewModelScope.launch {
        repository.getMeal(id)
            .onStart {
                _loadingMeal.value = true
            }
            .collect {
                _loadingMeal.value = false
                _meal.value = (it as Result.Success).data
            }
    }

    fun insertIntoCart() = viewModelScope.launch {
        _addingToCart.value = true
        if (repository.insertIntoCart(_meal.value!!)) {
            _cartText.value = "Added To Cart"
        } else {
            _cartText.value = "Failed to add"
        }
        _addingToCart.value = false
    }
}
