package com.example.resdelivery.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.requests.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.Result
import com.example.resdelivery.util.SessionManagement
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

@ExperimentalCoroutinesApi
class DetailRepository(
    private val sessionManagement: SessionManagement,
    private val database: FirebaseFirestore,
    private val foodApi: ApiService,
    private val dao: MealsDao
) {

    private val _cartInserted = MutableLiveData<Boolean>()

    val cartInserted: LiveData<Boolean>
        get() = _cartInserted

    fun getMeal(id: String): Flow<Result<Meal>> {
        return flow {
            try {
                val meal = foodApi.getMeal(id).meal
                dao.insertMeal(meal)
                emit(Result.Success(meal))
            } catch (e: Exception) {
                Timber.e(e)
                emit(Result.Error(dao.getMealById(id)))
            }
        }.flowOn(Dispatchers.IO)
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