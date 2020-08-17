package com.example.resdelivery.features.map


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codingwithmitch.googlemaps2018.models.ClusterMarker
import com.example.resdelivery.R
import com.example.resdelivery.databinding.FragmentMapBinding
import com.example.resdelivery.models.Branch
import com.example.resdelivery.util.C
import com.example.resdelivery.util.C.Companion.MAP_VIEW_BUNDLE_KEY
import com.example.resdelivery.util.SessionManagement
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.GeoApiContext
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MapFragment : Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener {


    @Inject
    lateinit var sessionManager: SessionManagement
    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()

    //Map
    private lateinit var googleMap: GoogleMap
    private lateinit var mapBoundary: LatLngBounds
    private lateinit var clusterManager: ClusterManager<ClusterMarker>

    //    private lateinit var clusterManagerRenderer: MyClusterManagerRenderer
    private lateinit var geoApiContext: GeoApiContext
    private var clusterMarkers: MutableList<ClusterMarker> = mutableListOf()
    private var shopBranches = mutableListOf<Branch>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        binding.deliverHereButton.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                "Soon",
                Toast.LENGTH_SHORT
            ).show()
        }
        setUpMapView(savedInstanceState)
        subscribeToObserver()
        //setCameraView()
        return binding.root
    }

    private fun subscribeToObserver() {
        viewModel.branches.observe(viewLifecycleOwner, Observer { branches ->
            if (viewModel.getDone()!!) {
//                addMapMarkers(branches)
                shopBranches = branches as MutableList<Branch>
                viewModel.done()
            }
        })
    }

//    private fun addMapMarkers(branches: List<Branch>) {
//
//        clusterManager = ClusterManager(activity!!.applicationContext, googleMap)
//        clusterManagerRenderer = MyClusterManagerRenderer(
//            activity!!,
//            googleMap,
//            clusterManager
//        )
//        clusterManager.renderer = clusterManagerRenderer
//
//        //googleMap.setOnInfoWindowClickListener(this);
//
//        for (branch in branches) {
//
//            Timber.d("addMapMarkers: location: " + branch.location.toString())
//
//            val snippet = "Determine route to ${branch.title} ?"
//            val picture = R.drawable.restaurant // set the default avatar
//            val newClusterMarker = ClusterMarker(
//                LatLng(
//                    branch.location!!.latitude,
//                    branch.location!!.longitude
//                ),
//                branch.title!!,
//                snippet,
//                picture
//            )
//            clusterManager.addItem(newClusterMarker)
//            clusterMarkers.add(newClusterMarker)
//
//        }
//        clusterManager.cluster()
//        // setCameraView()
//    }

    private fun setUpMapView(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        savedInstanceState?.let {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync(this)
        geoApiContext = GeoApiContext.Builder()
            .apiKey(C.GOOGLE_MAPS_API_KEY_2)
            .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }

        //TODO do something here
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        map?.isMyLocationEnabled = true
        map?.let {
            googleMap = it
        }
        setCameraView()
    }

    override fun onInfoWindowClick(marker: Marker?) {
        marker?.let {
        }
    }

    private fun setCameraView() {
        val latitude = sessionManager.getUserLatitude()
        val longitude = sessionManager.getUserLongitude()
        Timber.d("mohamed $latitude")
        Timber.d("mohamed $longitude")
        Timber.d("in GeoPoint")
        val bottomBoundary: Double = latitude?.minus(.1) ?: 0.0
        val leftBoundary: Double = longitude?.minus(.1) ?: 0.0
        val topBoundary: Double = latitude?.plus(.1) ?: 0.0
        val rightBoundary: Double = longitude?.plus(.1) ?: 0.0
        mapBoundary = LatLngBounds(
            LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, rightBoundary)
        )
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary, 0))
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latitude!!, longitude!!),
                17.0f
            )
        )

    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }


    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
