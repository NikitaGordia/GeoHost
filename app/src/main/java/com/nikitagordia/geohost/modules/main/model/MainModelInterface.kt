package com.nikitagordia.geohost.modules.main.model

import com.google.android.gms.maps.model.LatLng
import com.nikitagordia.geohost.modules.main.model.data.User

/**
 * Created by nikitagordia on 6/11/18.
 */

interface MainModelInterface {

    fun online(name: String, sub: MainModelSubscriber): String?

    fun offline(key: String)

    fun changeName(key: String, name: String)

    fun changeLocation(key: String, pos: LatLng)
}

interface MainModelSubscriber {

    fun onUserAdd(u: User)

    fun onUserRemove(u: User)

    fun onUserChange(u: User)

    fun onChangeLocation(u: User)
}