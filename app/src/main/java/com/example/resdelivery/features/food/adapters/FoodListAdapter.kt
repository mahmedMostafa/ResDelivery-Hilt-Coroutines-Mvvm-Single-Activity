package com.example.resdelivery.features.food.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.FoodListItemBinding
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.Meals
import timber.log.Timber
import kotlin.math.roundToInt

class FoodListAdapter(
    private val listener: OnItemClickListener? = null,
    private val meals: ArrayList<Meal>,
    private val glide : RequestManager
) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(image: ImageView, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FoodListItemBinding.inflate(layoutInflater, parent, false)
        return FoodViewHolder(binding, listener,glide)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = meals[position]
        holder.bind(item)
    }

    fun addMeals(items: Meals) {
        items.forEach {
            if (meals.isEmpty()) {
                if (!(meals.contains(it))) {
                    meals.add(0, it)
                }
            } else {
                if (!(meals.contains(it))) {
                    meals.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = meals.size

    fun getMeal(position: Int) = meals[position]

    inner class FoodViewHolder(
        val binding: FoodListItemBinding,
        val listener: OnItemClickListener?,
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
                Timber.d("Gemy Clicked item from adapter is ${meals[adapterPosition]}")
                listener?.onItemClick(binding.foodItemImage, adapterPosition)
            }
        }
    }

    class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {

        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }

    }

}