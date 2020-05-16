package com.example.resdelivery.ui.detail

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.requests.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.SessionManagement
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class DetailRepository(
    private val sessionManagement: SessionManagement,
    private val database: FirebaseFirestore,
    private val foodApi: ApiService,
    private val dao: MealsDao
) {

    private val _status = MutableLiveData<STATUS>()
    private val _meal = MutableLiveData<Meal>()
    private val _cartInserted = MutableLiveData<Boolean>()

    val cartInserted: LiveData<Boolean>
        get() = _cartInserted

    val status: LiveData<STATUS>
        get() = _status

    val meal: LiveData<Meal>
        get() = _meal

    suspend fun getMeal(mealId: String) = withContext(IO) {
        val resultDeferred = foodApi.getMeal(mealId)
        try {
            Timber.d("Loading from network")
            _status.postValue(STATUS.LOADING)
            val meal = resultDeferred.await().meal
            dao.insertMeal(meal)
            Timber.e("Loading from the cache ${dao.getMealById(mealId).ingredients[0]}")
            _status.postValue(STATUS.SUCCESS)
            _meal.postValue(meal)
        } catch (e: Exception) {
            Timber.e("Loading from the cache ${dao.getMealById(mealId).ingredients}")
            _status.postValue(STATUS.ERROR)
            _meal.postValue(dao.getMealById(mealId))
            Timber.e(e)
        }
    }

    fun insertIntoCart(meal: Meal) {
        val map = HashMap<String, Any>()
        map.put("id", meal.id)
        map.put("title", meal.title)
        map.put("imageUrl", meal.imageUrl)
        map.put("rate", meal.rate)
        sessionManagement.getValue(SessionManagement.KEY_USER_ID)?.let {
            database.collection("users/").document(it).collection("cart")
                .document(meal.id).set(map)
                .addOnCompleteListener { task ->
                    _cartInserted.value = task.isSuccessful
                }
        }
    }
}