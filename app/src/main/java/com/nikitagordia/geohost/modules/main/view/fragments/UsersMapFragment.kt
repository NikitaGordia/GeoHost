package com.nikitagordia.geohost.modules.main.view.fragments

import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.modules.main.model.data.User
import com.nikitagordia.geohost.modules.main.view.MainActivity
import com.nikitagordia.geohost.modules.main.viewmodel.Action
import com.nikitagordia.geohost.modules.main.viewmodel.Event
import com.nikitagordia.geohost.utils.HashColor


/**
 * Created by nikitagordia on 6/11/18.
 */

class UsersMapFragment : SupportMapFragment() {

    private var map: GoogleMap? = null
    private var isFirstCameraUpdate = true
    private var isFirst = true
    private var hasFocus = false

    private val users = mutableListOf<UserOnMap>()

    override fun onStart() {
        super.onStart()
        getMapAsync({
            map = it
            onEvent((activity as MainActivity).lastEvent)
        })
    }

    override fun onStop() {
        super.onStop()
        map?.clear()
        users.clear()
        isFirst = true
        hasFocus = false
    }

    fun onEvent(t: Event?) {
        if (t == null || map == null || !isAdded) return
        if (isFirst) {
            isFirst = false
            for (i in t.users) onUserAdd(i)
        } else {
            when(t.event) {
                Action.ADD -> onUserAdd(t.u)
                Action.REMOVE -> onUserRemove(t.u)
                Action.CHANGE -> onUserChange(t.u)
            }
        }
        updateFocus()
    }

    private fun updateFocus() {
        if (hasFocus) return
        val bounds = LatLngBounds.builder()
        var cnt = 0
        for (i in users)
            if (i.u.position != null) {
                bounds.include(i.u.position)
                cnt++
            }
        if (cnt == 0) return
        hasFocus = true
        val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)
        val update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin)
        if (isFirstCameraUpdate) {
            map?.animateCamera(update)
            isFirstCameraUpdate = false
        } else map?.moveCamera(update)
    }

    private fun onUserAdd(u: User) {
        if (u.position != null) {
            val m = map?.addMarker(MarkerOptions()
                    .position(u.position!!)
                    .title(u.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(HashColor.hash(u.name))))
            users += UserOnMap(u, m)
        } else users += UserOnMap(u, null)
    }

    private fun onUserRemove(u: User) {
        val m = users.find { it.u == u }
        m?.marker?.apply { remove()  }
        users.remove(m)
    }

    private fun onUserChange(u: User) {
        val m = users.find { it.u == u }
        if (m != null) {
            if (m.marker == null) {
                val mark = map?.addMarker(MarkerOptions()
                        .position(u.position!!)
                        .title(u.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(HashColor.hash(u.name))))
                m.marker = mark
            } else {
                m.marker?.apply {
                    position = u.position
                    title = u.name
                    setIcon(BitmapDescriptorFactory.defaultMarker(HashColor.hash(u.name)))
                }
            }
        }
        m?.u?.update(u)
    }

    companion object {

        fun getInstance() = UsersMapFragment()
    }
}

data class UserOnMap(val u: User, var marker: Marker?)