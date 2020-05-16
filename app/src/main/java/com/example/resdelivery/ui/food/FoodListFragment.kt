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
import com.example.resdelivery.util.C.Companion.PERMISSION_REQUEST_ACCESS_FINE_LOCATION
import com.example.resdelivery.util.C.Companion.PERMISSION_REQUEST_ENABLE_GPS
import org.koin.android.viewmodel.ext.android.getViewModel


/**
 * A simple [Fragment] subclass.
 */
class FoodListFragment : Fragment(), FoodListAdapter.OnItemClickListener {

    override fun onItemClick(position: Int) {
//        val extras = FragmentNavigatorExtras(
//            view to "shared_container"
//        )
        this.findNavController().navigate(
            FoodListFragmentDirections.actionFoodListFragmentToDetailFragment(list[position].id)
            //extras
        )
    }

    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter
    private var list: List<Meal> = ArrayList()

    //Maps
    private var locationGranted: Boolean = false
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
        if (savedInstanceState == null)
            checkMaps()
    }

    private fun checkMaps() {
        if (checkMapServices()) {
            if (locationGranted) {
                viewModel.refreshMeals()
                viewModel.getLastKnownLocation()
            } else {
                getLocationPermission()
            }
        }
    }

    private fun setUpToolbar() {
        setHasOptionsMenu(true)
        binding.toolBar.title = "Meals"
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
    }

    private fun subscribeToObserver() {

        viewModel.foodList.observe(viewLifecycleOwner, Observer { meals ->
            meals?.let {
                foodAdapter.submitList(meals)
                list = meals
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            status?.let {
                when (it) {
                    FoodApiStatus.LOADING -> binding.progressBar.visibility = View.VISIBLE
                    FoodApiStatus.SUCCESS -> binding.progressBar.visibility = View.INVISIBLE
                    FoodApiStatus.ERROR -> binding.progressBar.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        foodAdapter = FoodListAdapter(this)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = foodAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    private fun shower() {
        var x = 20
        while (x-- != 0) {
            val container = binding.star.parent as ViewGroup
            val containerW = container.width
            val containerH = container.height
            val starW: Float = binding.star.width.toFloat()
            val starH: Float =  binding.star.height.toFloat()

            val newStar = AppCompatImageView(activity!!)
            newStar.setImageResource(R.drawable.ic_star)
            newStar.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            container.addView(newStar)
            //set the size of the star to be a random number ( from .1x to 1.6x of its default size)
            newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
            newStar.scaleY = newStar.scaleX
            //random x position
            //This code uses the width of the star to position it from half-way off the screen on the left (-starW / 2)
            // to half-way off the screen on the right (with the star positioned at (containerW - starW / 2).
            newStar.translationX = Math.random().toFloat() * containerW - starW / 2
            //to move the star from off the screen at the top to off the screen at the bottom
            val mover =
                ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, containerH + starH,-starH)
           // val vibrate = ObjectAnimator.ofFloat(newStar,View.TRANSLATION_X,Math.random().toFloat() * 2f + -4f)
            //accelerate the movement
            mover.interpolator = AccelerateInterpolator(Math.random().toFloat() * 1f + .5f)
            //the star will rotate a random amount between 0 and 1080 degrees (three times around)
            val rotator =
                ObjectAnimator.ofFloat(newStar, View.ROTATION, (Math.random() * 1080).toFloat())
            rotator.interpolator = LinearInterpolator()
            //AnimatorSet is basically a group of animations, along with instructions on when to run those animations
            val set = AnimatorSet()
            set.playTogether(mover, rotator)
            set.duration = (Math.random() * 3000 + 2000).toLong() // from 500 to 1600 ml
            //and don't forget to remove the animation once it's done
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator?) {
                    container.removeView(newStar)
                }
            })
            set.start()
        }
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
            R.id.action_search -> shower()
        }
        return super.onOptionsItemSelected(item)
    }


    //this is to enable the GPS permissions if it's not enabled
    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                val enableGpsIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, PERMISSION_REQUEST_ENABLE_GPS)
            }
        val alert = builder.create()
        alert.show()
    }

    private fun checkMapServices(): Boolean {
        if (isServicesOk()) {
            if (isMapsEnabled()) return true
        }
        return false
    }

    // STEP 1 -> this method is used to determine weather or not the device can use google services
    private fun isServicesOk(): Boolean {
        Timber.d("isServicesOK: checking google services version")

        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

        when {
            available == ConnectionResult.SUCCESS -> {
                //everything is fine and the user can make map requests
                Timber.d("isServicesOK: Google Play Services is working")
                return true
            }
            GoogleApiAvailability.getInstance().isUserResolvableError(available) -> {
                //an error occured but we can resolve it
                Timber.d("isServicesOK: an error occured but we can fix it")
                val dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(activity, available, ERROR_DIALOG_REQUEST)
                dialog.show()
            }
            else -> Toast.makeText(
                activity,
                "You can't make map requests",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }


    // STEP 2 -> this method is used to determine weather or not the GPS enabled on the device
    private fun isMapsEnabled(): Boolean {
        Timber.d("isMapsEnabled gets called")
        val manager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            buildAlertMessageNoGps()
            Timber.d("gps is not enabled")
            return false
        }
        Timber.d("gps is enabled")
        return true
    }

    //STEP 3 -> ask for the location permission
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationGranted = true
            viewModel.refreshMeals()
            Timber.d("I get called")
            viewModel.getLastKnownLocation()
        } else {
            Timber.d("Why i'm not geting called")
            //The result will run onRequestPermissionResult method
            //make sure this is just requestPermissions in a (fragment)
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Timber.d("onRequestPermissionResult is called")
        locationGranted = false
        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("location granted")
                    locationGranted = true
                    viewModel.refreshMeals()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult: called.")
        when (requestCode) {
            PERMISSION_REQUEST_ENABLE_GPS -> {
                if (locationGranted) {
                    viewModel.refreshMeals()
                    viewModel.getLastKnownLocation()
                } else {
                    getLocationPermission()
                }
            }
        }

    }
}
