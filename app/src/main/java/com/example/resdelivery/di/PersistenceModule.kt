package com.example.resdelivery.di

import androidx.room.Room
import com.example.resdelivery.data.local.MealsDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val persistenceModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            MealsDatabase::class.java,
            "meals.db"
        ).build()
    }

    single { get<MealsDatabase>().getMealsDao() }
}