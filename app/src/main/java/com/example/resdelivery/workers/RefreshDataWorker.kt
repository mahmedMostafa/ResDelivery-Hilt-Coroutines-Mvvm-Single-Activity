package com.example.resdelivery.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.resdelivery.features.food.domain.FoodListRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), KoinComponent {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val repository: FoodListRepository by inject()
        return try {
            repository.refreshMealsInBackground()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}