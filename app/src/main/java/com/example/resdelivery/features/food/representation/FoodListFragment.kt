package com.example.resdelivery.features.food.representation


import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FragmentFoodListBinding
import com.example.resdelivery.features.food.adapters.FoodListAdapter
import com.example.resdelivery.features.food.adapters.LoadStateAdapter
import com.example.resdelivery.util.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FoodListFragment : Fragment(), FoodListAdapter.OnItemClickListener {

    @Inject
    lateinit var glide: RequestManager
    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter

    private val viewModel: FoodListViewModel by viewModels()
//
//    private fun searchMeals(query: String) {
//        lifecycleScope.launch {
//            viewModel.searchMeals(query).collectLatest {
//                Timber.d("Gemy Called new PagingData")
//                foodAdapter.submitData(it)
//            }
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_food_list,
            container,
            false
        )
        init()
        return binding.root
    }

    private fun init() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setUpToolbar()
        setUpRecyclerView()
        setupRefreshLayout()
//        searchMeals("Bacon")
        viewModel.getMeals("Bacon")
        viewModel.meals.observe(viewLifecycleOwner, Observer {
            foodAdapter.submitData(lifecycle, it)
        })
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
//            viewModel.searchMeals("Pizza")
        }
    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)
        binding.toolBar.title = "Meals"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
    }

    private fun setUpRecyclerView() {
        foodAdapter = FoodListAdapter(this, glide)
        binding.recyclerView.apply {
            adapter = foodAdapter.withLoadStateFooter(LoadStateAdapter { foodAdapter.retry() })
        }
        postponeEnterTransition()
        binding.recyclerView.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
        foodAdapter.addLoadStateListener { loadState ->
            // Show loading spinner during initial load or refresh.
            binding.swipeRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
            // Only show the list if refresh succeeds.
//            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading

            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            binding.retryButton.setOnClickListener { foodAdapter.retry() }

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.append as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "Error is ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onItemClick(image: ImageView, position: Int) {
        //this is to tell the navigation which views i want to share with the (transition name)
        val currentMeal = foodAdapter.getMeal(position)
        val extras = FragmentNavigatorExtras(image to currentMeal.imageUrl)
        findNavController().navigateSafe(
            R.id.foodListFragment,
            FoodListFragmentDirections.actionFoodListFragmentToDetailFragment(
                currentMeal.id,
                currentMeal.imageUrl
            ), extras
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cartFragment -> this.findNavController().navigate(
                FoodListFragmentDirections.actionFoodListFragmentToCartFragment()
            )
            R.id.action_log_out -> {
                viewModel.logOutUser()
                this.findNavController()
                    .navigate(FoodListFragmentDirections.actionFoodListFragmentToLoginFragment())
            }
            R.id.action_mode -> {
                // Get new mode.
                val mode =
                    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_NO
                    ) {
                        AppCompatDelegate.MODE_NIGHT_YES
                    } else {
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    }

                // Change UI Mode
                AppCompatDelegate.setDefaultNightMode(mode)
                return true
            }
        }
        return true
    }
}
