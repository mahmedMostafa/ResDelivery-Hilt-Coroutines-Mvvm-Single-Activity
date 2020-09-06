package com.example.resdelivery.features.food.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.FoodListItemBinding
import com.example.resdelivery.features.food.adapters.viewholders.FoodViewHolder
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.Meals
import timber.log.Timber
import kotlin.math.roundToInt

class FoodListAdapter(
    private val listener: OnItemClickListener? = null,
    private val glide: RequestManager
) : PagingDataAdapter<Meal, ViewHolder>(MealDiffCallback()) {


    interface OnItemClickListener {
        fun onItemClick(image: ImageView, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        return FoodViewHolder.create(parent, listener, glide)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            (holder as FoodViewHolder).bind(it)
        }
    }

    fun getMeal(position: Int) = getItem(position)!!

    class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {

        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }

    }
}