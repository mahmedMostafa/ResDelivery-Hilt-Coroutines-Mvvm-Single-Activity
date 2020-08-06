package com.example.resdelivery.di

import com.example.resdelivery.features.cart.CartViewModel
import com.example.resdelivery.features.detail.representation.DetailViewModel
import com.example.resdelivery.features.food.representation.FoodListViewModel
import com.example.resdelivery.features.login.LoginViewModel
import com.example.resdelivery.features.map.MapViewModel
import com.example.resdelivery.features.register.RegisterViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel { CartViewModel(get(), get()) }

    viewModel {
        DetailViewModel(
            get()
        )
    }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { RegisterViewModel(get(), get(), get()) }

    viewModel { MapViewModel(get()) }

    viewModel {
        FoodListViewModel(
            get()
        )
    }
}