package com.example.resdelivery.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val mealId: String,
    val prevKey: Int?,
    val nextKey: Int?
)