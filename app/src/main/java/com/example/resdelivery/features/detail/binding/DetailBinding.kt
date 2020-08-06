package com.example.resdelivery.features.detail.binding

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.resdelivery.models.Meal
import kotlin.math.roundToInt


@BindingAdapter("rating")
fun mealRating(text: TextView, meal: Meal?) {
    meal?.let {
        text.text = meal.rate.roundToInt().toString()
    }
}

@BindingAdapter("ingredients")
fun mealIngredients(layout: LinearLayout, meal: Meal?) {
    meal?.let {
        layout.removeAllViews()
        meal.ingredients.let {
            for (item in it) run {
                val textView = TextView(layout.context).apply {
                    text = item
                    textSize = 15F
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                layout.addView(textView)
            }
        }
    }
}