package com.example.resdelivery.di

import android.content.Context
import androidx.room.Room
import com.example.resdelivery.data.local.MealsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object PersistenceModule {

    @Singleton
    @Provides
    fun provideRoomSingleton(
        @ApplicationContext context: Context
    ): MealsDatabase {
        return Room.databaseBuilder(
            context,
            MealsDatabase::class.java,
            "meals.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(database: MealsDatabase) = database.getMealsDao()
}