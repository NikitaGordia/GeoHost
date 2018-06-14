package com.nikitagordia.geohost.modules.main.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.nikitagordia.geohost.modules.main.model.MainModelInterface
import com.nikitagordia.geohost.modules.main.model.MainModelSubscriber
import com.nikitagordia.geohost.modules.main.model.data.User
import com.nikitagordia.geohost.modules.main.model.remote.firebase.FirebaseMainModel

/**
 * Created by nikitagordia on 6/11/18.
 */

class MainViewModel : MainViewModelInterface, MainModelSubscriber, ViewModel() {

    val model: MainModelInterface = FirebaseMainModel()

    var key: String? = null
    private val users = mutableListOf<User>()
    override val events = MutableLiveData<Event>()

    override fun online(name: String): String {
        key = model.online(name, this)
        return key!!
    }

    override fun offline() {
        key?.apply { model.offline(this) }
        key = null
        users.clear()
        events.value = null
    }

    override fun changeName(name: String) {
        key?.apply { model.changeName(this, name) }
    }

    override fun changeLocation(pos: LatLng) {
        key?.apply { model.changeLocation(this, pos) }
    }

    override fun onUserAdd(u: User) {
        users += u
        events.value = Event(Action.ADD, u, users)
    }

    override fun onUserRemove(u: User) {
        users.remove(u)
        events.value = Event(Action.REMOVE, u, users)
    }

    override fun onUserChange(u: User) {
        updateUser(u)
    }

    override fun onChangeLocation(u: User) {
        updateUser(u)
    }

    private fun updateUser(u: User) {
        users.forEachIndexed { index, user -> if (user == u) { users.get(index).update(u); return@forEachIndexed } }
        events.value = Event(Action.CHANGE, u, users)
    }
}