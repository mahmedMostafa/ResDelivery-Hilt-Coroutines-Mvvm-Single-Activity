package com.example.resdelivery.ui.food


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.resdelivery.databinding.FragmentFoodListBinding
import com.example.resdelivery.models.Meal
import android.widget.Toast
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.example.resdelivery.util.C.Companion.ERROR_DIALOG_REQUEST
import timber.log.Timber
import android.content.Intent
import android.content.pm.PackageManager
import android.content.Context
import android.location.LocationManager
import android.provider.Settings
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.resdelivery.R
import com.example.resdelivery.models.Meals
import com.example.resdelivery.util.C.Companion.PERMISSION_REQUEST_ACCESS_FINE_LOCATION
import com.example.resdelivery.util.C.Companion.PERMISSION_REQUEST_ENABLE_GPS
import com.example.resdelivery.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.getViewModel


/**
 * A simple [Fragment] subclass.
 */
@ExperimentalCoroutinesApi
class FoodListFragment : Fragment(), FoodListAdapter.OnItemClickListener {

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
        setUpToolbar()
        setUpRecyclerView()
        subscribeToObserver()

        return binding.root
    }

    //b/c this gets called only once as i don't want to reload the data
    //i'm not sure if it's the best practice
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)
        binding.toolBar.title = "Meals"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
    }

    private fun subscribeToObserver() {
        viewModel.meals.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        receivedData(it.data)
                    }
                    is Result.Error -> {
                        receivedData(it.data)
                        Toast.makeText(
                            requireContext(),
                            "Loading meals from cache",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        })
    }

    private fun receivedData(meals: Meals) {
        binding.progressBar.visibility = View.INVISIBLE
        list = meals
        foodAdapter.submitList(list)
    }

    private fun setUpRecyclerView() {
        foodAdapter = FoodListAdapter(this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = foodAdapter
        }
    }


    override fun onItemClick(position: Int) {
        this.findNavController().navigate(
            FoodListFragmentDirections.actionFoodListFragmentToDetailFragment(list[position].id)
        )
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
        }
        return super.onOptionsItemSelected(item)
    }
}
