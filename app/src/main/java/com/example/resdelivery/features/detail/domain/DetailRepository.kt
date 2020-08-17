package com.example.resdelivery.features.detail.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.network.ApiService
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.Result
import com.example.resdelivery.util.SessionManagement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DetailRepository @Inject constructor(
    private val sessionManagement: SessionManagement,
    private val database: FirebaseFirestore,
    private val foodApi: ApiService,
    private val dao: MealsDao
) {

    fun getMeal(id: String): Flow<Result<Meal>> {
        return flow {
            try {
                val meal = foodApi.getMeal(id).meal
                dao.insertMeal(meal)
                emit(Result.Success(meal))
            } catch (e: Exception) {
                Timber.e(e)
                emit(Result.Success(dao.getMealById(id)))
            }
        }.flowOn(Dispatchers.IO)
    }

    //we will just return a boolean indicating if the task succeeded or not
    suspend fun insertIntoCart(meal: Meal): Boolean {
        val map = HashMap<String, Any>()
        map.put("id", meal.id)
        map.put("title", meal.title)
        map.put("imageUrl", meal.imageUrl)
        map.put("rate", meal.rate)
        return sessionManagement.getValue(SessionManagement.KEY_USER_ID)?.let {
            try {
                database.collection("users/")
                    .document(it)
                    .collection("cart")
                    .document(meal.id)
                    .set(map)
                    .await()
                true
            } catch (e: FirebaseFirestoreException) {
                false
            }
        } ?: false
    }
}