package com.example.resdelivery.features.food.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FoodListItemBinding
import com.example.resdelivery.databinding.LoadStateFooterBinding

class LoadStateViewHolder(
    private val binding: LoadStateFooterBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = "Something Went wrong!"
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState !is LoadState.Loading
        binding.errorMsg.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LoadStateFooterBinding.inflate(layoutInflater, parent, false)
            return LoadStateViewHolder(binding, retry)
        }
    }
}