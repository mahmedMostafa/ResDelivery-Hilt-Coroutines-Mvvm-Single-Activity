package com.example.resdelivery.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.resdelivery.databinding.CartListItemBinding
import com.example.resdelivery.models.Meal
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.math.roundToInt

class CartAdapter(
    private var list: MutableList<Meal>,
    private val listener: OnItemClickListener? = null
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    interface OnItemClickListener {

        fun onItemClick(position: Int)
        fun onCloseImgClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartListItemBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding, listener)
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
        val listener: OnItemClickListener?
    ) : RecyclerView.ViewHolder(binding.root), KoinComponent {

        private val glide: RequestManager = get()

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