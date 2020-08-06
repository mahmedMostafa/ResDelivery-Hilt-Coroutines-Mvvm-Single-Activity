package com.example.resdelivery.features.detail.representation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.resdelivery.R
import com.example.resdelivery.databinding.DetailFragmentBinding
import com.example.resdelivery.extensions.setSharedElementTransitionOnEnter
import com.example.resdelivery.extensions.startEnterTransitionAfterLoadingImage
import com.example.resdelivery.models.Meal
import com.example.resdelivery.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
class DetailFragment : Fragment() {

    private lateinit var binding: DetailFragmentBinding
    private var mealId: String = ""
    private lateinit var currentMeal: Meal
    private lateinit var viewModel: DetailViewModel
    private val glide: RequestManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        requireContext().theme.applyStyle(R.style.DetailStyle, true)
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        val args = arguments?.let { DetailFragmentArgs.fromBundle(it) }
        setupToolBar()
        setSharedElementTransitionOnEnter()
        postponeEnterTransition()
        val image = args?.imageUrl ?: ""
        binding.mealImage.transitionName = image
        startEnterTransitionAfterLoadingImage(glide, image, binding.mealImage)
        mealId = args?.mealId ?: ""
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel = getViewModel()
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.getMeal(mealId)
        viewModel.meal.observe(viewLifecycleOwner, Observer {
            currentMeal = it
            binding.meal = it
        })
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        binding.toolBar.setupWithNavController(findNavController())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cart) {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToCartFragment()
            )
            return true
        }
        return true
    }

}
