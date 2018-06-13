package com.nikitagordia.geohost.modules.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.SupportMapFragment
import com.nikitagordia.geohost.R
import com.nikitagordia.geohost.modules.main.viewmodel.Event


/**
 * Created by nikitagordia on 6/11/18.
 */

class UsersMapFragment : SupportMapFragment() {

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
    }

    override fun onStart() {
        super.onStart()
    }

    fun onEvent(t: Event) {

    }

    companion object {

        fun getInstance() = UsersMapFragment()
    }
}