package com.example.resdelivery.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.resdelivery.features.food.domain.FoodListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class RefreshDataWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: FoodListRepository
) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            repository.refreshMealsInBackground()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}