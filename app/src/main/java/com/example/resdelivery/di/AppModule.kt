package com.example.resdelivery.di

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.resdelivery.R
import com.example.resdelivery.util.C.Companion.PREF_NAME
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {

    single { androidContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }

    single { get<SharedPreferences>().edit() }

    single { SessionManagement(get(), get()) }

    single { FirebaseAuth.getInstance() }

    single { FirebaseFirestore.getInstance() }

    //TODO change the context later
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single {
        RequestOptions
            .placeholderOf(R.drawable.white_background)
            .error(R.drawable.white_background)
    }

    single {
        Glide.with(androidContext())
            .setDefaultRequestOptions(get())
    }
}