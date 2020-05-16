package com.example.resdelivery.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.resdelivery.R
import com.example.resdelivery.databinding.DetailFragmentBinding
import com.example.resdelivery.models.Meal
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.math.roundToInt

class DetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: DetailFragmentBinding
    private var mealId: String = ""
    private lateinit var currentMeal: Meal
    private lateinit var viewModel: DetailViewModel
    private val glide: RequestManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment, container, false)

        val args = arguments?.let {
            DetailFragmentArgs.fromBundle(it)
        }
        mealId = args?.mealId ?: ""
        subscribeToObserver()
        return binding.root
    }

    private fun subscribeToObserver() {
        viewModel = getViewModel()
        viewModel.getMeal(mealId)
        viewModel.meal.observe(viewLifecycleOwner, Observer { meal ->
            meal?.let {
                if (viewModel.status.value == STATUS.SUCCESS) {
                    binding.progressBar.visibility = View.INVISIBLE
                    currentMeal = it
                    bindProperties(it)
                }else{
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it == STATUS.LOADING) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        })

        viewModel.cartInserted.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.buttonProgress.visibility = View.INVISIBLE
                binding.buttonText.visibility = View.VISIBLE
                binding.buttonText.text = getString(R.string.added_to_Cart)
            }
        })
    }

    private fun bindProperties(meal: Meal) {
        binding.cartImage.setOnClickListener(this)
        binding.backImage.setOnClickListener(this)
        binding.addToCartLayout.setOnClickListener(this)
        glide.load(meal.imageUrl)
            .into(binding.mealImage)
        binding.mealTitle.text = meal.title
        binding.mealRate.text = meal.rate.roundToInt().toString()
        setIngredients(meal)
        binding.backImage.visibility = View.VISIBLE
        binding.addToCartLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun setIngredients(meal: Meal) {
        binding.ingredientsContainer.removeAllViews()
        meal.ingredients.let {
            for (item in it) run {
                val textView = TextView(activity).apply {
                    text = item
                    textSize = 15F
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                binding.ingredientsContainer.addView(textView)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.back_image -> this.findNavController().popBackStack()

            R.id.add_to_cart_layout -> {
                binding.buttonText.visibility = View.INVISIBLE
                binding.buttonProgress.visibility = View.VISIBLE
                viewModel.insertIntoCart(currentMeal)
            }

            R.id.cart_image -> this.findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToCartFragment()
            )
        }
    }

}
