package com.example.resdelivery.util

import com.codingwithmitch.googlemaps2018.models.ClusterMarker
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.clustering.Cluster
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.maps.GoogleMap


class MyClusterManagerRenderer(
    context: Context, googleMap: GoogleMap,
    clusterManager: ClusterManager<ClusterMarker>
) : DefaultClusterRenderer<ClusterMarker>(context, googleMap, clusterManager) {

    private val iconGenerator: IconGenerator
    private val imageView: ImageView
    private val markerWidth: Int
    private val markerHeight: Int

    init {
        // initialize cluster item icon generator
        iconGenerator = IconGenerator(context.applicationContext)
        imageView = ImageView(context.applicationContext)
        markerWidth = 45
        markerHeight = 45
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        val padding = 0
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)

    }

    /**
     * Rendering of the individual ClusterItems
     * @param item
     * @param markerOptions
     */
    override fun onBeforeClusterItemRendered(item: ClusterMarker, markerOptions: MarkerOptions) {

        imageView.setImageResource(item.iconPicture)
        val icon = iconGenerator.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.title)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<ClusterMarker>?): Boolean {
        return false
    }

}