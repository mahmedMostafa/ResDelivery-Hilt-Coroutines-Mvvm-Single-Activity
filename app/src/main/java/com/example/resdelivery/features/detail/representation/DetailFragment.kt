package com.example.resdelivery.features.detail.representation

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import com.example.resdelivery.R
import com.example.resdelivery.databinding.DetailFragmentBinding
import com.example.resdelivery.extensions.setSharedElementTransitionOnEnter
import com.example.resdelivery.extensions.startEnterTransitionAfterLoadingImage
import com.example.resdelivery.models.Meal
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var binding: DetailFragmentBinding
    private var mealId: String = ""
    private lateinit var currentMeal: Meal
    private val viewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    override fun onResume() {
        super.onResume()
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        requireActivity().window.statusBarColor = Color.TRANSPARENT;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
//        val window = requireActivity().window
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
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
