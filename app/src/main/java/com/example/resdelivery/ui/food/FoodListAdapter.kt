package com.example.resdelivery.ui.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.FoodListItemBinding
import com.example.resdelivery.models.Meal
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.math.roundToInt

class FoodListAdapter(
    private val listener: OnItemClickListener? = null
) : ListAdapter<Meal, FoodListAdapter.FoodViewHolder>(MealDiffCallback()) {


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FoodListItemBinding.inflate(layoutInflater, parent, false)
        return FoodViewHolder(binding, listener)
        //return FoodViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun getMeal(position: Int): Meal {
        return getItem(position)
    }

    inner class FoodViewHolder(
        val binding: FoodListItemBinding,
        val listener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root), KoinComponent {

        val glide: RequestManager = get()

        fun bind(item: Meal) {
            //for the sake of this animation to work kinda properly i animated the image only not the container
            //ViewCompat.setTransitionName(binding.foodItemImage, item.title) // Step 1 : For the container animation
            glide.load(item.imageUrl)
                .into(binding.foodItemImage)
            binding.foodItemTitle.text = item.title
            binding.foodItemRating.text = item.rate.roundToInt().toString()
            binding.root.setOnClickListener {
                listener?.onItemClick(adapterPosition)
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