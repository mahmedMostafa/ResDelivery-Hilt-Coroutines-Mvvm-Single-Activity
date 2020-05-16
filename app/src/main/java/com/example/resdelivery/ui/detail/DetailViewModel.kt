package com.example.resdelivery.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resdelivery.models.Meal
import com.example.resdelivery.data.network.requests.ApiService
import com.example.resdelivery.util.SessionManagement
import com.example.resdelivery.util.SessionManagement.Companion.KEY_USER_ID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

enum class STATUS { LOADING, SUCCESS, ERROR }

class DetailViewModel(
    private val repository: DetailRepository
) : ViewModel() {

    val cartInserted: LiveData<Boolean>
        get() = repository.cartInserted

    val status: LiveData<STATUS>
        get() = repository.status

    val meal: LiveData<Meal>
        get() = repository.meal


    fun getMeal(mealId: String) = viewModelScope.launch {
        repository.getMeal(mealId)
    }

    fun insertIntoCart(meal: Meal) {
        repository.insertIntoCart(meal)
    }
}
