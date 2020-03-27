package com.example.resdelivery.ui.food


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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
            com.example.resdelivery.R.layout.fragment_food_list,
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
                viewModel.getFood()
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
        inflater.inflate(com.example.resdelivery.R.menu.food_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.example.resdelivery.R.id.cartFragment -> this.findNavController().navigate(
                FoodListFragmentDirections.actionFoodListFragmentToCartFragment()
            )
            com.example.resdelivery.R.id.action_log_out -> {
                viewModel.logOutUser()
                this.findNavController()
                    .navigate(FoodListFragmentDirections.actionFoodListFragmentToLoginFragment())
            }
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
            viewModel.getFood()
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
                    viewModel.getFood()
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
                    viewModel.getFood()
                    viewModel.getLastKnownLocation()
                } else {
                    getLocationPermission()
                }
            }
        }

    }
}
