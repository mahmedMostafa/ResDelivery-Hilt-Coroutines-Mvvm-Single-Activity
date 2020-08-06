package com.example.resdelivery.di

import com.example.resdelivery.features.detail.domain.DetailRepository
import com.example.resdelivery.features.food.domain.FoodListRepository
import org.koin.dsl.module

val repositoryModule = module {

    single {
        FoodListRepository(
            get(),
            get(),
            get(),
            get()
        )
    }

    single {
        DetailRepository(
            get(),
            get(),
            get(),
            get()
        )
    }
}