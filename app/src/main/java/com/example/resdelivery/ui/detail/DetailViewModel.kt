package com.example.resdelivery.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.Result
import com.example.resdelivery.util.Result.Loading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onStart


@ExperimentalCoroutinesApi
class DetailViewModel(
    private val repository: DetailRepository
) : ViewModel() {

    fun getMeal(id: String): LiveData<Result<Meal>> {
        return repository.getMeal(id)
            .onStart { emit(Loading) }
            .asLiveData()
    }

    fun insertIntoCart(meal: Meal) {
        repository.insertIntoCart(meal)
    }
}
