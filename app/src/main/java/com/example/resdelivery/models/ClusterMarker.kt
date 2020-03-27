package com.codingwithmitch.googlemaps2018.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/*
    we didn't use the data class b/c of the ClusterItem interface
 */
class ClusterMarker : ClusterItem {

    private var position: LatLng? = null
    private var title: String? = null
    private var snippet: String? = null
    var iconPicture: Int = 0

    constructor(position: LatLng, title: String, snippet: String, iconPicture: Int) {
        this.position = position
        this.title = title
        this.snippet = snippet
        this.iconPicture = iconPicture
    }

    constructor() {}

    override fun getPosition(): LatLng? {
        return position
    }

    fun setPosition(position: LatLng) {
        this.position = position
    }

    override fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    fun setSnippet(snippet: String) {
        this.snippet = snippet
    }
}
