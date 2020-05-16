package com.example.resdelivery.di

import com.example.resdelivery.ui.detail.DetailRepository
import com.example.resdelivery.ui.food.FoodListRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { FoodListRepository(get(), get(), get(), get()) }

    single { DetailRepository(get(), get(), get(), get()) }
}