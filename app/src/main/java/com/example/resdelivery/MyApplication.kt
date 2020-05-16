package com.example.resdelivery

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.resdelivery.di.*
import com.example.resdelivery.workers.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit


class MyApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        //setting up timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        delayInit()
        //setting up koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
            modules(viewModelModule)
            modules(networkModule)
            modules(persistenceModule)
            modules(repositoryModule)
        }
    }

    private fun delayInit() = applicationScope.launch {
        setupRecurringWork()
    }

    private fun setupRecurringWork() {

        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // like wifi
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(false)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true) // it means that the user isn't actively using the device
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constrains).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}