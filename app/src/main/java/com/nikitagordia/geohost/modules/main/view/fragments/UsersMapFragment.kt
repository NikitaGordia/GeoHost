package com.nikitagordia.geohost.modules.main.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.modules.main.model.data.Position
import com.nikitagordia.geohost.modules.main.view.MainActivity
import com.nikitagordia.geohost.modules.main.viewmodel.Action
import com.nikitagordia.geohost.modules.main.viewmodel.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.nikitagordia.geohost.utils.HashColor


/**
 * Created by nikitagordia on 6/11/18.
 */

class UsersMapFragment : SupportMapFragment() {

    private var map: GoogleMap? = null
    var isFirstCameraUpdate = true
    var isFirst = true

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        isFirst = true

        getMapAsync({
            map = it
            onEvent((activity as MainActivity).lastEvent)})
    }

    fun onEvent(t: Event?) {
        if (t == null || map == null) return
        map?.clear()
        val bounds = LatLngBounds.builder()
        for (i in t.users) {
            val pos = i.position ?: continue
            val ltln = LatLng(pos.lat, pos.lon)
            map?.addMarker(MarkerOptions().position(ltln).title(i.name).icon(BitmapDescriptorFactory.defaultMarker(HashColor.hash(i.name))))
            bounds.include(ltln)
        }
        if (!isAdded) return
        if (isFirst) {
            isFirst = false
            val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)
            val update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin)
            if (isFirstCameraUpdate) {
                map?.animateCamera(update)
                isFirstCameraUpdate = false
            } else map?.moveCamera(update)
        }
    }

    companion object {

        fun getInstance() = UsersMapFragment()
    }
}