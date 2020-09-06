package com.example.resdelivery.features.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.CartListItemBinding
import com.example.resdelivery.models.Meal
import kotlin.math.roundToInt

class CartAdapter(
    private var list: MutableList<Meal>,
    private val listener: OnItemClickListener? = null,
    private val glide : RequestManager
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onCloseImgClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartListItemBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding, listener,glide)
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.onBind(list.get(position))
    }

    override fun getItemCount(): Int = list.size

    fun setCartItems(items: MutableList<Meal>) {
        list = items
        notifyDataSetChanged()
    }

    class CartViewHolder(
        val binding: CartListItemBinding,
        val listener: OnItemClickListener?,
        val glide : RequestManager
    ) : RecyclerView.ViewHolder(binding.root){

        fun onBind(meal: Meal) {
            binding.cartRate.text = meal.rate.roundToInt().toString()
            binding.cartTitle.text = meal.title
            glide.load(meal.imageUrl)
                .into(binding.cartImage)
        }

        init {
            binding.root.setOnClickListener {
                listener?.onItemClick(adapterPosition)
            }
            binding.closeImage.setOnClickListener {
                listener?.onCloseImgClick(adapterPosition)
            }
        }
    }
}