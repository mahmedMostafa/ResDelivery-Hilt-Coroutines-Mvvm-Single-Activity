package com.example.resdelivery

import android.app.Application
import timber.log.Timber
import com.example.resdelivery.di.appModule
import com.example.resdelivery.di.networkModule
import com.example.resdelivery.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //setting up timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        //setting up koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
            modules(viewModelModule)
            modules(networkModule)
        }
    }
}