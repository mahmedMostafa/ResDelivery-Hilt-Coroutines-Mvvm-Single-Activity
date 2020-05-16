package com.example.resdelivery.models

import android.os.Parcelable
import androidx.room.*
import com.example.resdelivery.data.local.IngredientsTypeConverter
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "meals")
data class Meal(

    @PrimaryKey
    @Json(name = "recipe_id")
    var id: String = "",

    var title: String = "",

    @Json(name = "image_url")
    @ColumnInfo(name = "image_url")
    var imageUrl: String = "",

    @Json(name = "social_rank")
    var rate: Double = 0.0,

    //b/c this only exists in the second api request so begin with empty array
    @Json(name = "ingredients")
    @TypeConverters(IngredientsTypeConverter::class)
    var ingredients: Array<String> = arrayOf()

) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Meal

        if (id != other.id) return false
        if (title != other.title) return false
        if (imageUrl != other.imageUrl) return false
        if (rate != other.rate) return false
        if (!ingredients.contentEquals(other.ingredients)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + rate.hashCode()
        result = 31 * result + ingredients.contentHashCode()
        return result
    }
}