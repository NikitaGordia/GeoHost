package com.nikitagordia.geohost.modules.main.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.nikitagordia.geohost.modules.main.model.data.User

/**
 * Created by nikitagordia on 6/11/18.
 */


interface MainViewModelInterface {

    val events: MutableLiveData<Event>

    fun online(name: String): String

    fun offline()

    fun changeName(name: String)

    fun changeLocation(pos: LatLng)
}

class Event(val event: Action, val u: User, val users: MutableList<User>)

enum class Action { ADD, REMOVE, CHANGE }