package com.example.resdelivery.data.local

import androidx.room.TypeConverter
import com.example.resdelivery.models.Meal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IngredientsTypeConverter {

    //remember that it's sometimes an array and a list
    @TypeConverter
    fun fromString(value: String?): Array<String>? {
        val listType = object :
            TypeToken<Array<String?>?>() {}.type
        return Gson().fromJson<Array<String>>(value, listType)
    }

    @TypeConverter
    fun fromList(list: Array<String?>?): String? {
        return Gson().toJson(list)
    }

}