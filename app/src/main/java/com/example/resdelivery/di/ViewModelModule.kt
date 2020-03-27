package com.example.resdelivery.di

import com.example.resdelivery.ui.cart.CartViewModel
import com.example.resdelivery.ui.detail.DetailViewModel
import com.example.resdelivery.ui.food.FoodListViewModel
import com.example.resdelivery.ui.login.LoginViewModel
import com.example.resdelivery.ui.map.MapViewModel
import com.example.resdelivery.ui.register.RegisterViewModel
import com.google.android.gms.maps.MapView
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel { CartViewModel(get(), get()) }

    viewModel { DetailViewModel(get(), get(), get()) }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { RegisterViewModel(get(), get(), get()) }

    viewModel { MapViewModel(get()) }

    viewModel { FoodListViewModel(get(), get() , get()) }
}