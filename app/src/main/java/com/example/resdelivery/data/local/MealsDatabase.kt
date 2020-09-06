package com.example.resdelivery.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.RemoteKeys

@Database(entities = [Meal::class, RemoteKeys::class], version = 2, exportSchema = false)
@TypeConverters(IngredientsTypeConverter::class)
abstract class MealsDatabase : RoomDatabase() {

    abstract fun getMealsDao(): MealsDao
    abstract fun getRemoteKeysDao(): RemoteKeysDao

    //probably won't need this since we are using hilt
    companion object {
        @Volatile
        private var instance: MealsDatabase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context): MealsDatabase {
            return instance ?: buildDatabase(context.applicationContext)
        }

        private fun buildDatabase(context: Context): MealsDatabase {
            return synchronized(LOCK) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MealsDatabase::class.java,
                    "meals.db"
                )
                    .build()
            }
        }
    }




}