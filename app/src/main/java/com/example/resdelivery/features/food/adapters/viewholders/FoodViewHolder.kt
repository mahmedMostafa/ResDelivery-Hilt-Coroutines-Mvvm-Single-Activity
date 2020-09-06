package com.example.resdelivery.features.food.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.FoodListItemBinding
import com.example.resdelivery.features.food.adapters.FoodListAdapter
import com.example.resdelivery.models.Meal
import timber.log.Timber
import kotlin.math.roundToInt


class FoodViewHolder(
    val binding: FoodListItemBinding,
    val listener: FoodListAdapter.OnItemClickListener?,
    val glide: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Meal) {
        //for the sake of this animation to work kinda properly i animated the image only not the container
        //ViewCompat.setTransitionName(binding.foodItemImage, item.title) // Step 1 : For the container animation
        glide.load(item.imageUrl)
            .into(binding.foodItemImage)
        binding.foodItemImage.transitionName = item.imageUrl
        binding.foodItemTitle.text = item.title
        binding.foodItemRating.text = item.rate.roundToInt().toString()
        binding.root.setOnClickListener {
            listener?.onItemClick(binding.foodItemImage, adapterPosition)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            listener: FoodListAdapter.OnItemClickListener?,
            glide: RequestManager
        ): FoodViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FoodListItemBinding.inflate(layoutInflater, parent, false)
            return FoodViewHolder(binding, listener, glide)
        }
    }
}