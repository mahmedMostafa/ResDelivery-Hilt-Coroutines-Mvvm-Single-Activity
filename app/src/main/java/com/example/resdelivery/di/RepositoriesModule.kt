package com.example.resdelivery.di

import androidx.paging.ExperimentalPagingApi
import com.example.resdelivery.data.local.MealsDao
import com.example.resdelivery.data.local.MealsDatabase
import com.example.resdelivery.data.network.ApiService
import com.example.resdelivery.features.detail.domain.DetailRepository
import com.example.resdelivery.features.food.domain.FoodListRepository
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@InstallIn(ApplicationComponent::class)
@Module
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideFoodListRepository(
        foodApi: ApiService,
        fusedLocationProviderClient: FusedLocationProviderClient,
        sessionManagement: SessionManagement,
        database : MealsDatabase
    ): FoodListRepository {
        return FoodListRepository(foodApi, fusedLocationProviderClient, sessionManagement, database)
    }

    @Singleton
    @Provides
    fun provideDetailsRepository(
        sessionManagement: SessionManagement,
        firebaseFirestore: FirebaseFirestore,
        foodApi: ApiService,
        dao: MealsDao
    ): DetailRepository {
        return DetailRepository(sessionManagement, firebaseFirestore, foodApi, dao)
    }
}