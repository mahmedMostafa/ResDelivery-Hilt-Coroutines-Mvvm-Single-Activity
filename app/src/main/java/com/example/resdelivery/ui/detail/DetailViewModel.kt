package com.example.resdelivery.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.resdelivery.models.Meal
import com.example.resdelivery.network.requests.ApiService
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
    private val sessionManagement: SessionManagement,
    private val database : FirebaseFirestore,
    private val foodApi : ApiService
) : ViewModel() {

    private val _status = MutableLiveData<STATUS>()
    private val _meal = MutableLiveData<Meal>()
    private val _cartInserted = MutableLiveData<Boolean>()

    val cartInserted : LiveData<Boolean>
        get() = _cartInserted

    val status: LiveData<STATUS>
        get() = _status

    val meal: LiveData<Meal>
        get() = _meal


    private val job = Job()

    private val coroutineScope = CoroutineScope(job + Main)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMeal(mealId: String) {
        coroutineScope.launch {
            val resultDeferred = foodApi.getMeal(mealId)
            try {
                _status.value = STATUS.LOADING
                val meal = resultDeferred.await().meal
                _status.value = STATUS.SUCCESS
                _meal.value = meal
            } catch (e: Exception) {
                _status.value = STATUS.ERROR
                Timber.e(e.message)
            }
        }
    }

    fun insertIntoCart(meal : Meal) {
        val map = HashMap<String,Any>()
        map.put("id",meal.id)
        map.put("title",meal.title)
        map.put("imageUrl",meal.imageUrl)
        map.put("rate",meal.rate)
        sessionManagement.getValue(KEY_USER_ID)?.let {
            database.collection("users/").document(it).collection("cart")
                .document(meal.id).set(map)
                .addOnCompleteListener{task->
                    _cartInserted.value = task.isSuccessful
                }
        }
    }
}
