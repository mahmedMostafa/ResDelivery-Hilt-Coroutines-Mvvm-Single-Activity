package com.example.resdelivery.features.food.representation


import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FragmentFoodListBinding
import com.example.resdelivery.features.food.adapters.FoodListAdapter
import com.example.resdelivery.models.Meal
import com.example.resdelivery.models.Meals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.getViewModel
import timber.log.Timber


@ExperimentalCoroutinesApi
class FoodListFragment : Fragment(),
    FoodListAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter
    private var list: List<Meal> = ArrayList()

    private lateinit var viewModel: FoodListViewModel

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
        changeColor(R.color.colorPrimaryDark)
        return binding.root
    }


    fun changeColor(resourseColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.statusBarColor = ContextCompat.getColor(
                requireActivity().applicationContext,
                resourseColor
            )
        }
        val bar: android.app.ActionBar? = requireActivity().actionBar
        bar?.setBackgroundDrawable(ColorDrawable(resources.getColor(resourseColor)))
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        foodAdapter = FoodListAdapter(this, arrayListOf())
        viewModel = getViewModel()
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setUpToolbar()
        setUpRecyclerView()
        subscribeToObserver()
        setupRefreshLayout()
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getMeals(1)
        }
    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)
        binding.toolBar.title = "Meals"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
    }

    private fun subscribeToObserver() {
        viewModel.meals.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.d("Whole data is $it")
                receivedData(it)
            }
        })
    }

    private fun setUpChips() {
        binding.chipGroup.setOnCheckedChangeListener { chipGroup, checkedId ->
            when (checkedId) {
                R.id.chechen_chip -> {
                    binding.chechenChip.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
                R.id.chechen_chip -> {

                }
            }
        }
    }

    private fun receivedData(meals: Meals) {
        list = meals
        foodAdapter.addMeals(meals)
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = foodAdapter
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == foodAdapter.itemCount.minus(1)) {
                    if (!isLoading()) {
                        Timber.d("Is it loading ? ${isLoading()}")
                        viewModel.getMoreMeals()
                    }
                }
            }
        })
        postponeEnterTransition()
        binding.recyclerView.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    fun isLoading() = binding.paginationProgress.isVisible

    override fun onItemClick(image: ImageView, position: Int) {
        //this is to tell the navigation which views i want to share with the (transition name)
        val currentMeal = foodAdapter.getMeal(position)
        val extras = FragmentNavigatorExtras(image to currentMeal.imageUrl)
        this.findNavController().navigate(
            FoodListFragmentDirections.actionFoodListFragmentToDetailFragment(
                currentMeal.id,
                currentMeal.imageUrl
            ), extras
        )
        Timber.d("Gemy Clicked item is ${list[position]}")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
