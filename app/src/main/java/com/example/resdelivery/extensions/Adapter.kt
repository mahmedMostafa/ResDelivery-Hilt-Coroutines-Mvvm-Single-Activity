package com.example.resdelivery.extensions

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resdelivery.models.Meal
import com.example.resdelivery.ui.food.FoodListAdapter
import kotlin.math.roundToInt


//@BindingAdapter("rateText")
//fun TextView.setRate(rate : Double){
//    text = rate.roundToInt().toString()
//}
//
//@BindingAdapter("mealImage")
//fun ImageView.setMealImage(view: AppCompatImageView, url: String) {
//    Glide.with(view.context)
//        .load(url)
//        .into(view)
//}
//
//
//@BindingAdapter("listData")
//fun binRecyclerAdapter(recyclerView: RecyclerView, data : List<Meal>){
//    val adapter =recyclerView.adapter as FoodListAdapter
//    adapter.submitList(data)
//}